package com.id11688025.majorassignment;

/**
 * Provides constant static variables for use across the application
 */
public class Constants
{
    public static final int REQUEST_CODE_LOAD_SHADER = 0;
    public static final int REQUEST_CODE_MODEL_CHANGED = 1;

    public static final int RESULT_CODE_LOADED = 0;
    public static final int RESULT_CODE_EXIT = 1;
    public static final int RESULT_CODE_CHANGED = 2;

    public static final String EXTRA_SHADER_SOURCE = "shader_source";

    public static final String EXTRA_COMPILER_LOG = "compile_log";

    public static final String PREFERENCE_MODEL = "model_preference";
    public static final String PREFERENCE_AUTO_ROTATE = "auto_rotate_preference";

    public static final String FALLBACK_MODEL = "models/dbunny.obj";
    public static final String FALLBACK_SHADER = "shaders/phong_basic.fs.glsl";
}
