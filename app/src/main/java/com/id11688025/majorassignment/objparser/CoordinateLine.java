package com.id11688025.majorassignment.objparser;

import com.id11688025.majorassignment.math.Vector3;

/**
 * A line of an OBJ file that contains a coordinate.
 */
public class CoordinateLine extends OBJLine
{
    /** The coordinate represented by this line */
    private Vector3 coordinate;

    public CoordinateLine(LineType type, String lineString) {
        super(type, lineString);
        coordinate = this.parse();
    }

    /** Parse this line's string as a coordinate */
    private Vector3 parse()
    {
        // Split the string by whitespaces
        String[] parts = lineString.split("\\s+");

        // The coordinate is now split into its coordinates as strings
        // Parse each string to a float.
        float[] coordinates = new float[parts.length];
        for(int i=0; i < parts.length; i++)
            coordinates[i] = Float.parseFloat(parts[i]);

        // Create a vector from the float array
        return new Vector3(coordinates);
    }

    /** Get the coordinate described by this line */
    public Vector3 getCoordinate() {
        return coordinate;
    }
}
