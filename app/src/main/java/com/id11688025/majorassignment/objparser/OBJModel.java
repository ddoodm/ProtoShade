package com.id11688025.majorassignment.objparser;

import com.id11688025.majorassignment.ContentManager;
import com.id11688025.majorassignment.MainActivity;
import com.id11688025.majorassignment.math.Vector3;
import com.id11688025.majorassignment.math.VertexPositionNormalTextureTangent;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A mechanism for loading and storing Wavefront OBJ format models.
 */
public class OBJModel
{
    /** The class tag for Logcat tracing */
    private static final String TAG = "OBJ_MODEL";

    /** The list of miscellaneous lines (comments, ...) */
    private ArrayList<OBJLine> miscLines;
    /** The list of vertex coordinates that compose the model */
    private ArrayList<CoordinateLine> positionLines;
    /** The list of normal vectors that compose the model */
    private ArrayList<CoordinateLine> normalLines;
    /** The list of texture coordinates that compose the model */
    private ArrayList<CoordinateLine> textureLines;
    /** The list of faces that link vertices */
    private ArrayList<FaceLine> faceLines;

    /** The array of model vertices */
    private VertexPositionNormalTextureTangent[] vertices;

    /**
     * Create and parse a new OBJ model.
     * @param content The content manager.
     * @param path The path to the asset file of the OBJ model.
     * @param asyncTask The OBJLoaderTask that displays the progress dialog.
     */
    public OBJModel(ContentManager content, final String path, OBJLoaderTask asyncTask)
    {
        miscLines = new ArrayList<OBJLine>();
        positionLines = new ArrayList<CoordinateLine>();
        normalLines = new ArrayList<CoordinateLine>();
        textureLines = new ArrayList<CoordinateLine>();
        faceLines = new ArrayList<FaceLine>();

        this.load(content, path, asyncTask);
    }

    /**
     * Parse a Wavefront OBJ model file from an Android resource.
     * @param content The content manager.
     * @param path The path to the asset file of the OBJ model.
     * @param asyncTask The OBJLoaderTask that displays the progress dialog.
     */
    private void load(ContentManager content, final String path, OBJLoaderTask asyncTask)
    {
        // Obtain the number of lines in the file for the progress dialog
        asyncTask.setMaxProgress(content.countLines(path));

        // Obtain a data stream from the asset
        BufferedReader lineReader = content.getBufferedReader(path);

        try
        {
            // Read each line of the file
            int i = 0;
            String currentLine = "";
            while ((currentLine = lineReader.readLine()) != null)
            {
                loadLine(currentLine);
                asyncTask.setProgress(++i);
            }

            // Dispose the streams
            lineReader.close();

            // Fill the vertex array
            populateVertexArray();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /** Populate the array of vertices with the newly parsed vertex data */
    private void populateVertexArray()
    {
        int numberOfVertices = faceLines.size() * FaceLine.VERTS_PER_FACE;
        vertices = new VertexPositionNormalTextureTangent[numberOfVertices];

        // Populate array
        for(int i=0; i<numberOfVertices; i++)
        {
            vertices[i] = faceLines.get(
                    i / FaceLine.VERTS_PER_FACE)
                    .getVertex(i % FaceLine.VERTS_PER_FACE);
        }
    }

    /** Parse a line of the OBJ file by determining the line type */
    private void loadLine(String line)
    {
        // Determine the line type by reading the first substring
        String typeName = line.split("\\s+")[0];

        // The actual data contained within the line (minus the line type)
        String lineData = line.substring(typeName.length()).trim();

        // Comment
        if(typeName.equals("#"))
            miscLines.add(new OBJLine(OBJLine.LineType.COMMENT, lineData));

        // Vertex
        else if(typeName.equals("v"))
            positionLines.add(new CoordinateLine(OBJLine.LineType.VERTEX, lineData));

        // Vertex Normal
        else if(typeName.equals("vn"))
            normalLines.add(new CoordinateLine(OBJLine.LineType.NORMAL, lineData));

        // Vertex Texture Coordinate
        else if(typeName.equals("vt"))
            textureLines.add(new CoordinateLine(OBJLine.LineType.TEXCOORD, lineData));

        // Face group (mesh)
        else if(typeName.equals("g"))
            miscLines.add(new OBJLine(OBJLine.LineType.GROUP, lineData));

        // Face
        else if(typeName.equals("f"))
            faceLines.add(new FaceLine(OBJLine.LineType.FACE, lineData, this));
    }

    /** Get the position at the specified index as a Vector3 */
    public Vector3 getPosition(int index)
    {
        return positionLines.get(index).getCoordinate();
    }

    /** Get the normal at the specified index as a Vector3 */
    public Vector3 getNormal(int index)
    {
        return normalLines.get(index).getCoordinate();
    }

    /** Get the texture coordinate at the specified index as a Vector3.
     * The vector must be truncated to a Vector2 manually. OBJ handles
     * texture coordinates in a 3D frame.*/
    public Vector3 getTexture(int index)
    {
        return textureLines.get(index).getCoordinate();
    }

    /** Get the array of un-indexed vertices that compose this model */
    public VertexPositionNormalTextureTangent[] getVertices()
    {
        return vertices;
    }
}
