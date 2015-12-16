package com.id11688025.majorassignment;

import com.id11688025.majorassignment.math.*;

/**
 * A static camera which does not update per-frame.
 * The Camera should be extended in order to provide
 * per-frame updates in the desired form.
 */
public class Camera
{
    /** The composited view matrix ready for per-vertex
     * multiplication by the shader program. */
    private Matrix view;

    /** The perspective / orthographic projection matrix */
    private Matrix projection;

    /** The camera's position in 3D Cartesian space */
    private Vector3 position;
    /** The look-at target */
    private Vector3 target;
    /** The camera's direction in 3D Cartesian space (normalized) */
    private Vector3 direction;
    /** The vector used for constructing the coordinate system.
     * Usually (0,1,0). */
    private Vector3 up;

    /** The camera's field of view (viewing angle) in radians. Default is 60'. */
    private float fieldOfView = (float)Math.toRadians(60.0);

    /** The width (in pixels) of the viewport. */
    private int viewportWidth = 1;
    /** The height (in pixels) of the viewport. */
    private int viewportHeight = 1;

    /**
     * Create a camera at "position" facing "target"
     * @param position The camera's position
     * @param target The target to look at
     * @param up The vector that faces up. Usually (0,1,0)
     */
    public Camera (Vector3 position, Vector3 target, Vector3 up)
    {
        this.position = position;
        this.target = target;
        this.direction = Vector3.normalize( target.subtract(position) );
        this.up = up;

        createLookAt();
        createPerspectiveProjection();
    }

    /** Create / rebuild the look-at matrix for this camera */
    public void createLookAt()
    {
        view = Matrix.createLookAt(
                position,
                Vector3.add(position, direction),
                up);
    }

    /** Create / rebuild the perspective projection matrix for this camera */
    public void createPerspectiveProjection()
    {
        projection = Matrix.createPerspectiveFov(
                fieldOfView,
                (float)viewportWidth / (float)viewportHeight,
                0.1f, 100.0f);
    }

    /** The composited view matrix ready for per-vertex
     * multiplication by the shader program. */
    public Matrix getView() {
        return view;
    }

    /** The composited view matrix ready for per-vertex
     * multiplication by the shader program. */
    public void setView(Matrix view) {
        this.view = view;
    }

    /** The perspective / orthographic projection matrix */
    public Matrix getProjection() {
        return projection;
    }

    /** The perspective / orthographic projection matrix */
    public void setProjection(Matrix projection) {
        this.projection = projection;
    }

    /** The camera's position in 3D Cartesian space */
    public Vector3 getPosition() {
        return position;
    }

    /** The camera's position in 3D Cartesian space */
    public void setPosition(Vector3 position) {
        this.position = position;

        // Update direction
        this.direction = Vector3.normalize( target.subtract(position) );
    }

    /** The camera's direction in 3D Cartesian space (normalized) */
    public Vector3 getDirection() {
        return direction;
    }

    /** The camera's direction in 3D Cartesian space (normalized) */
    public void setDirection(Vector3 direction) {
        this.direction = direction;
    }

    /** The vector used for constructing the coordinate system.
     * Usually (0,1,0). */
    public Vector3 getUpVector() {
        return up;
    }

    /** The vector used for constructing the coordinate system.
     * Usually (0,1,0). */
    public void setUpVector(Vector3 up) {
        this.up = up;
    }

    /** The width (in pixels) of the viewport. */
    public void setViewportWidth(int viewportWidth) {
        this.viewportWidth = viewportWidth;
    }

    /** The height (in pixels) of the viewport. */
    public void setViewportHeight(int viewportHeight) {
        this.viewportHeight = viewportHeight;
    }
}
