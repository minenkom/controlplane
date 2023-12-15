package com.controlplane.controlplane;

import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.ui.content.ContentFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.*;

public class MyToolWindowFactory implements ToolWindowFactory {
    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final Gson GSON = new Gson();

    private JPanel projectList;

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false);
        panel.add(createMyToolWindowContent(project));
        toolWindow.getContentManager().addContent(
            ContentFactory.getInstance().createContent(panel, "", false)
        );
    }

    private Component createMyToolWindowContent(Project project) {
        JPanel panel = new JPanel();
        this.projectList = new JPanel();
        this.projectList.setLayout(new BoxLayout(this.projectList, BoxLayout.Y_AXIS));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel loadingLabel = new JLabel("Loading...");
        loadingLabel.setVisible(false);

        JTextField searchField = new JTextField();
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, searchField.getPreferredSize().height));
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadingLabel.setVisible(true);
                new SwingWorker<List<GitLabProject>, Void>() {
                    @Override
                    protected List<GitLabProject> doInBackground() throws Exception {
                        // Call method to fetch project list, using text from searchField
                        return fetchProjectList(searchField.getText());
                    }

                    @Override
                    protected void done() {
                        try {
                            // Update UI with fetched project list
                            updateProjectList(panel, project, get());
                        } catch (Exception ex) {
                            // Handle exception
                            ex.printStackTrace();
                        } finally {
                            // Hide loading label
                            loadingLabel.setVisible(false);
                        }
                    }
                }.execute();
            }
        });
        panel.add(searchField);

        panel.add(loadingLabel);

        updateProjectList(panel, project, new ArrayList<>());

        return panel;
    }

    private List<GitLabProject> fetchProjectList(String text) {
        GitLabTokenSettings settings = GitLabTokenSettings.getInstance();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(settings.gitLabApiBaseUrl).newBuilder();
        urlBuilder.addQueryParameter("search", text);
        urlBuilder.addQueryParameter("per_page", "50");
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("PRIVATE-TOKEN", settings.gitLabToken)
                .build();
        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                Type type = new TypeToken<List<GitLabProject>>(){}.getType();
                List<GitLabProject> gitLabProjects = GSON.fromJson(response.body().string(), type);
                return gitLabProjects;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<GitLabProject>();
    }

    private JPanel updateProjectList(JPanel panel, Project project, List<GitLabProject> gitLabProjects) {
        JPanel projectList = this.projectList;
        this.projectList.removeAll();

        JLabel checkingOutLabel = new JLabel("Checking out...");
        projectList.add(checkingOutLabel);
        checkingOutLabel.setVisible(false);

        for (GitLabProject gitLabProject : gitLabProjects) {
            JLabel projectLabel = new JLabel(gitLabProject.getPathWithNamespace());
            projectLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            projectLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    new SwingWorker<Boolean, Void>() {
                        @Override
                        protected Boolean doInBackground() throws Exception {
                            try {
                                GitLabTokenSettings settings = GitLabTokenSettings.getInstance();
                                checkingOutLabel.setVisible(true);
                                File checkoutDir = new File(project.getBasePath(), gitLabProject.getName());
                                this.deleteDirectory(checkoutDir.toPath());
                                Git.cloneRepository()
                                        .setURI(gitLabProject.getHttpUrlToRepo())
                                        .setDirectory(checkoutDir)
                                        .setCredentialsProvider(new UsernamePasswordCredentialsProvider("Private-Token", settings.gitLabToken))
                                        .call();
                                // Get a reference to the VirtualFileManager
                                VirtualFileManager virtualFileManager = VirtualFileManager.getInstance();
                                VirtualFile file = virtualFileManager.refreshAndFindFileByUrl(VfsUtilCore.pathToUrl(checkoutDir.toString()));
                                if (file != null) {
                                    file.refresh(false, false);
                                }
                            } catch (GitAPIException gitAPIException) {
                                gitAPIException.printStackTrace();
                            }

                            return true;
                        }

                        @Override
                        protected void done() {
                            try {
                                //
                                //
                            } catch (Exception ex) {
                                // Handle exception
                                ex.printStackTrace();
                            } finally {
                                checkingOutLabel.setVisible(false);
                            }
                        }


                        public void deleteDirectory(Path directory) throws IOException {
                            if (Files.exists(directory)) {
                                Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                                    @Override
                                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                        Files.delete(file);
                                        return FileVisitResult.CONTINUE;
                                    }

                                    @Override
                                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                                        Files.delete(dir);
                                        return FileVisitResult.CONTINUE;
                                    }
                                });
                            }
                        }
                    }.execute();



                }
            });
            projectList.add(projectLabel);
            // Add vertical padding after each project label, except the last one
            if (gitLabProjects.indexOf(gitLabProject) != gitLabProjects.size() - 1) {
                projectList.add(Box.createRigidArea(new Dimension(0, 10)));  // 10 pixel vertical padding
            }
        }
        panel.add(projectList);
        panel.revalidate();
        panel.repaint();

        return panel;
    }


    static class GitLabProject {
        private String name;
        private String http_url_to_repo;

        private String path_with_namespace;

        String getName() {
            return name;
        }

        String getHttpUrlToRepo() {
            return http_url_to_repo;
        }

        String getPathWithNamespace() {
            return path_with_namespace;
        }
    }
}