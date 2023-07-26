package com.controlplane.controlplane;
import com.intellij.openapi.options.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GitLabTokenConfigurable implements Configurable {
    private JTextField gitLabTokenField;
    private JTextField gitLabAPIBaseUrlField;
    private JPanel panel;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "GitLab Token Settings";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        gitLabTokenField = new JTextField();
        gitLabAPIBaseUrlField = new JTextField();
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("GitLab Token:"));
        panel.add(gitLabTokenField);
        panel.add(new JLabel("GitLab API Base URL:"));
        panel.add(gitLabAPIBaseUrlField);
        return panel;
    }

    @Override
    public boolean isModified() {
        GitLabTokenSettings settings = GitLabTokenSettings.getInstance();
        return !gitLabTokenField.getText().equals(settings.gitLabToken)
                || !gitLabAPIBaseUrlField.getText().equals(settings.gitLabApiBaseUrl);
    }


    @Override
    public void apply() {
        GitLabTokenSettings settings = GitLabTokenSettings.getInstance();
        settings.gitLabToken = gitLabTokenField.getText();
        settings.gitLabApiBaseUrl = gitLabAPIBaseUrlField.getText();
    }

    @Override
    public void reset() {
        GitLabTokenSettings settings = GitLabTokenSettings.getInstance();
        gitLabTokenField.setText(settings.gitLabToken);
        gitLabAPIBaseUrlField.setText(settings.gitLabApiBaseUrl);
    }
}