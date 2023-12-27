package com.amvera.cli.dto.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubRelease(
        @JsonProperty("tag_name")
        String tagName,
        boolean draft,
        boolean prerelease
) {
}
