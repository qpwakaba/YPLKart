package com.github.erozabesu.yplkart.object;

public class PermissionObject {

    /** パーミッションノード */
    private String permissionNode;

    public PermissionObject(String permissionNode) {
        this.setPermissionNode(permissionNode);
    }

    /** @return パーミッションノード */
    public String getPermissionNode() {
        return permissionNode;
    }

    /** @param permissionNode パーミッションノード */
    public void setPermissionNode(String permissionNode) {
        this.permissionNode = permissionNode;
    }
}
