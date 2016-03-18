package com.id11688025.majorassignment.storage;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.id11688025.majorassignment.ContentManager;

/**
 * The database that stores local (private user)
 * shaders. The underlying SQL database does not
 * store the actual shader data, instead, it
 * stores references to shader files.
 */
public class LocalShaderDatabase extends SQLiteOpenHelper
{
    /** The name of the database. */
    private static final String DATABASE_NAME = "shaders.db";

    /** The current version of the database structure.
     * Should be changed upon database re-structure. */
    private static final int DATABASE_VERSION = 3;

    /** The name given to the "shaders" table. */
    private static final String TABLE_SHADERS = "shaders";

    /** The SQLite query that creates the database. */
    private static final String QUERY_CREATE =
            "CREATE TABLE " + TABLE_SHADERS + " (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ShaderDescription.KEY_TITLE + " TEXT NOT NULL," +
                    ShaderDescription.KEY_ISREQUIRED + " isRequired INTEGER," +
                    ShaderDescription.KEY_PATH + " path TEXT NOT NULL" +
                    ")";

    /** The SQLite query that returns the number of rows
     * in the "shaders" table. */
    private static final String QUERY_COUNT =
            "SELECT count(*) FROM " + TABLE_SHADERS + ";";

    private ShaderDescription[] sampleShaders;

    /** Create an interface between the shader database and application.
     * @param context An application context.
     */
    public LocalShaderDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        createSampleShaders(new ContentManager(context));
    }

    private void createSampleShaders(ContentManager content)
    {
        sampleShaders = new ShaderDescription[]
        {
            new ShaderDescription("Phong & Rim", true, "shaders/phong_basic.fs.glsl" ),
            new ShaderDescription("Normals", true, "shaders/normals.fs.glsl" ),
            new ShaderDescription("Moving Light", true, "shaders/moving_light.fs.glsl" ),
            new ShaderDescription("Electric Sinusoid", true, "shaders/sinusoid.fs.glsl" ),
            new ShaderDescription("Tiled Texture", true, "shaders/tile_tex.fs.glsl" ),
        };
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Create the database
        db.execSQL(QUERY_CREATE);

        // Insert the sample shaders
        for(ShaderDescription shader : sampleShaders)
            db.insert(TABLE_SHADERS, null, shader.getContentValues());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Simply drop the table and re-create it.
        // TODO: Perform a responsible SQL database upgrade
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHADERS);
        onCreate(db);
    }

    /**
     * @return The number of shaders in the database.
     */
    public int count()
    {
        // Get a read-only instance of the database.
        SQLiteDatabase database = getReadableDatabase();

        // Execute query to resolve the number of rows in the table.
        Cursor allRows = database.rawQuery(QUERY_COUNT, null);

        // There are no rows
        if(!allRows.moveToFirst())
            return 0;

        // Return the integer result of count(*)
        return allRows.getInt(0);
    }

    /**
     * Add a shader to the database, and save its contents locally.
     * @param shaderDescription The shader that should be saved.
     */
    public void save(ShaderDescription shaderDescription)
    {
        // If an entry exists with the same path, do not write to the database.
        if(exists(shaderDescription.getPath()))
            return;

        // Obtain a writable database instance
        SQLiteDatabase database = getWritableDatabase();

        // Obtain the ContentValues that represent the passed Subject
        ContentValues values = shaderDescription.getContentValues();

        // Insert the new values into the table
        database.insert(TABLE_SHADERS, null, values);

        database.close();
    }

    /**
     * Determines whether any entries with the value of "path" exist.
     * @param path The path to the shader
     * @return True if the shader is already in the database
     */
    private boolean exists(String path)
    {
        String[] whereArgs = new String[] {path};

        // Get a read-only instance of the database.
        SQLiteDatabase database = getReadableDatabase();

        // Form a query to find the ShaderDescription with the specified path
        Cursor descriptionCursor = database.query(
                TABLE_SHADERS,      // Table name
                new String[]{       // Columns
                        ShaderDescription.KEY_PATH,},
                "path=?",           // Where clause
                whereArgs,          // Where args
                null,               // Group By
                null,               // Having
                null);              // Order By

        // Check that there is more than one row
        return descriptionCursor.getCount() > 0;
    }

    /**
     * Obtain the ShaderDescription with the specified
     * ID from the database, and load its contents.
     * @param id The ID of the ShaderDescription to return.
     * @return The ShaderDescription with the specified ID.
     */
    public ShaderDescription load(long id)
    {
        // Very important: SQL begins indexing at 1, not 0.
        id++;

        // Get a read-only instance of the database.
        SQLiteDatabase database = getReadableDatabase();

        // Form a query to find the ShaderDescription with the specified ID
        Cursor descriptionCursor = database.query(
                TABLE_SHADERS,      // Table name
                new String[]{       // Columns
                        ShaderDescription.KEY_TITLE,
                        ShaderDescription.KEY_ISREQUIRED,
                        ShaderDescription.KEY_PATH,},
                "_id=?",            // Where clause
                new String[]{       // Where args
                        Long.toString(id)},
                null,               // Group By
                null,               // Having
                null);              // Order By

        // There are no entries, so return nothing.
        if(!descriptionCursor.moveToFirst())
            return null;

        // Obtain values for only the first row (there should only be one).
        ContentValues values = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(descriptionCursor, values);

        // Return the values as a ShaderDescription
        return new ShaderDescription(values);
    }

    public void remove(long id)
    {
        // DANGER: Must increment ID
        id++;

        // Obtain a writable database instance
        SQLiteDatabase database = getWritableDatabase();

        // Delete from the database
        database.delete(
                TABLE_SHADERS,
                "_id=?",
                new String[]{ Long.toString(id) }
        );

        database.close();
    }

    /**
     * Obtains the ID of the element at the index of "position"
     */
    public long getIdOfItemAtPosition(int position)
    {
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT _id FROM " + TABLE_SHADERS + " LIMIT 1 OFFSET ?;",
                new String[]{Integer.toString(position)});

        c.moveToFirst();
        long id =  c.getLong(0);

        db.close();
        c.close();

        // SUBTRACT ONE - keep IDs 0 indexed
        return id - 1;
    }
}
