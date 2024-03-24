package com.xyberneo.layoutsheet;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.AndroidViewComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;

@SimpleObject(external = true)
@DesignerComponent(category = ComponentCategory.EXTENSION,
        description = "An extension to create LayoutDialog",
        iconName = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABmJLR0QA/wD/AP+gvaeTAAACNUlEQVQ4EWNgoCWInHVBO3rGOS18djBik8xecNVZRoSzUE9B0JbxP8P/iw/fH3ny5nv/1ATtvejq4QaErlrFzPtOLUtNhi/EWlPMVEaYixNZ8ZO3374fvf7q9I0nn9Z8Fbo1bXVY2F+QPKNDw34OVTnhMnVJXh8HHQlDYV52FpAELvzm888/B6+8OH/j2Zetdx6/6WRMm3f5eqiFnLqKFC/cNbg0I4vfefb5/+oTD28ySfJznPvy6zfjjvNPGV59/IGsBisbpGb7uacMP37/ZZTk5zzH9OXnH55vP/8yOGhLMDx7/40Bl0Ewja8+/GBw1JFg+PTjN8O333+5mdhZmX6YKgszXH74nuH9518MtpriKAYha7TTEmd4/ekHWC1IDysz40+mf//+Mx+7+ZqBmZmJwUZTjOHa4w9gg6w1xBjuv/rC8ACIQeIgjSA5EBukFqTn////TEw///y7I8TD/l5Nio/h9N23DH/+/WcwUxVmuPjgPcOfv/8YfgMxiA0SA8mduvOGAaRWkJvt/Y/f/+6CQz502kV1ZWGOGiMlQRdNWQGJS0DvgEJQT06AgRGo4tLDDwz/gQJ6coIMlx68e3n50cfdt99/a1qbYXgbKA2UgaLQqWdVtMT5KnUVBTyVJHglL9x7B5YxUBRiuPns44sbjz5tu/byU/vqbOM7YAkggWIAkA9GoZPOaalJ8ZSZqwi7/AeqOHX77Z5bz750rc4zugZWQCwRO/usZuTMSxrEqidLHQDkBwovbBqdDAAAAABJRU5ErkJggg==",
        nonVisible = true,
        version = 3)
public class LayoutSheet extends AndroidNonvisibleComponent
        implements DialogInterface.OnDismissListener, DialogInterface.OnShowListener {

    //Properties
    private final Context context;
    private float deviceDensity;
    private Dialog dialog;
    private float dimAmount = 0.2f;
    private int gravity;
    private boolean isCancelable;

    // Variables for corner radius
    private float RadiusTopLeft = 0;
    private float RadiusTopRight = 0;
    private float RadiusBottomLeft = 0;
    private float RadiusBottomRight = 0;

    public LayoutSheet(ComponentContainer container) {
        super(container.$form());
        this.context = container.$context();
        this.deviceDensity = container.$form().deviceDensity();
        initializeDialog();
    }

    // Initialize dialog
    private void initializeDialog() {
        this.dialog = new Dialog(this.context);
        this.dialog.getWindow().requestFeature(1);
        this.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        this.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Set transparent background
        this.dialog.setOnDismissListener(this);
    }

    @DesignerProperty(defaultValue = "false", editorType = "boolean")
    @SimpleProperty(description = "Set whether the dialog is cancelable or not")
    public void Cancelable(boolean cancelable) {
        this.isCancelable = cancelable;
        if (this.dialog != null) {
            this.dialog.setCancelable(cancelable);
        }
    }

    @SimpleProperty
    public boolean Cancelable() {
        return this.isCancelable;
    }

    @DesignerProperty(defaultValue = "0.5", editorType = "string")
    @SimpleProperty(description = "Set the dim amount")
    public void DimAmount(float d) {
        this.dimAmount = d;
    }

    @SimpleProperty
    public float DimAmount() {
        return this.dimAmount;
    }

    @DesignerProperty(defaultValue = "35", editorType = "int")
    @SimpleProperty(description = "Set the gravity [35 Middle] [16 Top] [80 Bottom]")
    public void Gravity(int g) {
        this.gravity = g;
    }

    //Show dialog
    @SimpleFunction
    public void Show() {
        if (this.dialog != null) {
            this.dialog.setCancelable(this.isCancelable);
            this.dialog.show();
        }
    }

    //Register view as Layout
    @SimpleFunction(description = "Register the given component as Layoutsheet")
    public void Register(AndroidViewComponent component, int height, int width) {
        View view = component.getView();
        ((ViewGroup) view.getParent()).removeView(view);
        if (this.dialog == null) {
            initializeDialog();
        }
        this.dialog.setContentView(view);
        WindowManager.LayoutParams params = this.dialog.getWindow().getAttributes();
        params.width = calculate(width);
        params.height = calculate(height);
        params.gravity = this.gravity;
        this.dialog.setCancelable(this.isCancelable);
        this.dialog.getWindow().setDimAmount(this.dimAmount);
        setRadiusCorner(); //Dar el curveado antes de que se muestre
        this.dialog.getWindow().setAttributes(params);
    }

    //Setter top left corner radius
    @DesignerProperty(defaultValue = "0", editorType = "float")
    @SimpleProperty(description = "Set the radius of the top left corner in pixels")
    public void RadiusTopLeft(float radius) {
        this.RadiusTopLeft = radius;
    }

    //Setter top right corner radius
    @DesignerProperty(defaultValue = "0", editorType = "float")
    @SimpleProperty(description = "Set the radius of the top right corner in pixels")
    public void RadiusTopRight(float radius) {
        this.RadiusTopRight = radius;
    }

    //Setter bottom left corner radius
    @DesignerProperty(defaultValue = "0", editorType = "float")
    @SimpleProperty(description = "Set the radius of the bottom left corner in pixels")
    public void RadiusBottomLeft(float radius) {
        this.RadiusBottomLeft = radius;
    }

    //Setter bottom right corner radius
    @DesignerProperty(defaultValue = "0", editorType = "float")
    @SimpleProperty(description = "Set the radius of the bottom right corner in pixels")
    public void RadiusBottomRight(float radius) {
        this.RadiusBottomRight = radius;
    }

    //Method to set corner radius to dialog
    private void setRadiusCorner() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && this.dialog != null) {
            float[] radii = new float[]{
                    RadiusTopLeft, RadiusTopLeft,
                    RadiusTopRight, RadiusTopRight,
                    RadiusBottomRight, RadiusBottomRight,
                    RadiusBottomLeft, RadiusBottomLeft
            };
            RoundRectShape roundRectShape = new RoundRectShape(radii, null, null);
            ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
            shapeDrawable.getPaint().setColor(Color.TRANSPARENT);
            this.dialog.getWindow().setBackgroundDrawable(shapeDrawable);
        }
    }

    //Calculate size screen
    public int calculate(int p) {
        return (p == -1 || p == -2) ? p : p > 0 ? (int) (p * context.getResources().getDisplayMetrics().heightPixels / 100.0f) : ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    //Dismiss dialog
    @SimpleFunction
    public void Dismiss() {
        if (this.dialog != null) {
            this.dialog.dismiss();
            this.dialog = null;
        }
    }

    //Check if dialog is showing
    @SimpleFunction
    public boolean IsShowing() {
        return this.dialog != null && this.dialog.isShowing();
    }

    //Event fired when dialog is dismissed
    @SimpleEvent(description = "Fired when dialog dismissed")
    public void Dismissed() {
        EventDispatcher.dispatchEvent(this, "Dismissed", new Object[0]);
    }

    //Event fired when dialog is shown
    @SimpleEvent(description = "Fired when dialog is shown")
    public void Shown() {
        EventDispatcher.dispatchEvent(this, "Shown", new Object[0]);
    }

    // Handler for dialog dismiss event
    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        Dismissed();
    }

    // Handler for dialog show event
    @Override
    public void onShow(DialogInterface dialogInterface) {
        Shown();
    }
}
