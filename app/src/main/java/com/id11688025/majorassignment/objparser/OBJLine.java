package com.id11688025.majorassignment.objparser;

/**
 * A line of an OBJ file.
 */
public class OBJLine
{
    /** The available types of readable lines of an OBJ model */
    public static enum LineType
    {
        COMMENT, VERTEX, NORMAL, TEXCOORD, GROUP, FACE
    }

    /** The type of this line */
    protected LineType type;

    /** The ASCII string value of this line */
    protected String lineString;

    public OBJLine(LineType type, String lineString)
    {
        this.type = type;
        this.lineString = lineString;
    }
}
