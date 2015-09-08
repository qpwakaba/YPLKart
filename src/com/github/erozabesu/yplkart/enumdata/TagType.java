package com.github.erozabesu.yplkart.enumdata;

public enum TagType {

    PLAYER("player", false),

    PLAYER_ARRAY("player", true),

    ITEM("item", false),

    PERMISSION("perm", false),

    RACE_TYPE("racetype", false),

    CIRCUIT("circuitname", false),

    CHARACTER("character", false),

    KART("kart", false),

    NUMBER("number", false),

    NUMBER_ARRAY("number", true),

    TEXT("text", false),

    TEXT_ARRAY("text", true),

    FLAG("flag", false);

    /** 置換するタグの文字列 */
    private String tagText;

    /** 扱うメッセージパーツが配列かどうか */
    private boolean isArray;

    private TagType(String tagText, boolean isArray) {
        this.setTagText(tagText);
        this.setArray(isArray);
    }

    /** @return 置換するタグの文字列 */
    public String getTagText() {
        return tagText;
    }

    /** @return 扱うメッセージパーツが配列かどうか */
    public boolean isArray() {
        return isArray;
    }

    /** @param tagText 置換するタグの文字列 */
    public void setTagText(String tagText) {
        this.tagText = tagText;
    }

    /** @param isArray 扱うメッセージパーツが配列かどうか */
    public void setArray(boolean isArray) {
        this.isArray = isArray;
    }
}
