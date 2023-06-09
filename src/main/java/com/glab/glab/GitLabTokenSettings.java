package com.glab.glab;

import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "GitLabTokenSettings", storages = {@Storage("gitlabTokenSettings.xml")})
public class GitLabTokenSettings implements PersistentStateComponent<GitLabTokenSettings> {
    public String gitLabToken = "";
    public String gitLabApiBaseUrl = "";

    @Nullable
    @Override
    public GitLabTokenSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull GitLabTokenSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public static GitLabTokenSettings getInstance() {
        return ServiceManager.getService(GitLabTokenSettings.class);
    }
}