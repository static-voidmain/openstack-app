package org.example.openstack.demo.data;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openstack4j.model.identity.v3.Token;

@Data
@NoArgsConstructor
public class UserInformation {
    private Token unscopedToken;
    private Token scopedToken;
    private String projectName;
    private String projectId;
    private String domain;

    @Builder
    public UserInformation(Token unscopedToken, Token scopedToken, String projectName, String projectId, String domain) {
        this.unscopedToken = unscopedToken;
        this.scopedToken = scopedToken;
        this.projectName = projectName;
        this.projectId = projectId;
        this.domain = domain;
    }
}
