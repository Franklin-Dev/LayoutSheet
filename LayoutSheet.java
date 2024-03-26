package com.xyberneo.layoutsheet;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.runtime.*;
import com.google.appinventor.components.common.*;

@DesignerComponent(version = 4,
        description = "An extension to create dialog",
        nonVisible = true,
        iconName = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABmJLR0QA/wD/AP+gvaeTAAACNUlEQVQ4EWNgoCWInHVBO3rGOS18djBik8xecNVZRoSzUE9B0JbxP8P/iw/fH3ny5nv/1ATtvejq4QaErlrFzPtOLUtNhi/EWlPMVEaYixNZ8ZO3374fvf7q9I0nn9Z8Fbo1bXVY2F+QPKNDw34OVTnhMnVJXh8HHQlDYV52FpAELvzm888/B6+8OH/j2Zetdx6/6WRMm3f5eqiFnLqKFC/cNbg0I4vfefb5/+oTD28ySfJznPvy6zfjjvNPGV59/IGsBisbpGb7uacMP37/ZZTk5zzH9OXnH55vP/8yOGhLMDx7/40Bl0Ewja8+/GBw1JFg+PTjN8O333+5mdhZmX6YKgszXH74nuH9518MtpriKAYha7TTEmd4/ekHWC1IDysz40+mf//+Mx+7+ZqBmZmJwUZTjOHa4w9gg6w1xBjuv/rC8ACIQeIgjSA5EBukFqTn////TEw///y7I8TD/l5Nio/h9N23DH/+/WcwUxVmuPjgPcOfv/8YfgMxiA0SA8mduvOGAaRWkJvt/Y/f/+6CQz502kV1ZWGOGiMlQRdNWQGJS0DvgEJQT06AgRGo4tLDDwz/gQJ6coIMlx68e3n50cfdt99/a1qbYXgbKA2UgaLQqWdVtMT5KnUVBTyVJHglL9x7B5YxUBRiuPns44sbjz5tu/byU/vqbOM7YAkggWIAkA9GoZPOaalJ8ZSZqwi7/AeqOHX77Z5bz750rc4zugZWQCwRO/usZuTMSxrEqidLHQDkBwovbBqdDAAAAABJRU5ErkJggg==",
        category = ComponentCategory.EXTENSION)
@SimpleObject(external=true)
public class LayoutSheet extends AndroidNonvisibleComponent implements DialogInterface.OnDismissListener, DialogInterface.OnShowListener {

    //Variables
    private float deviceDensity;
    private float dimAmount = 0.2f;
    private boolean isCancelable;
    private final Context context;
    private Dialog dialog;
    private int tipColor;

    public LayoutSheet(final ComponentContainer container) {
        super(container.$form());
        context = container.$context();
        deviceDensity = container.$form().deviceDensity();
        initializeDialog();
    }

    // Initialize dialog
    private void initializeDialog() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setOnDismissListener(this);
    }

    // Calculate screen size
    private int calculate(int p) {
        return (p == -1 || p == -2) ? p : p > 0 ? (int) (p * context.getResources().getDisplayMetrics().heightPixels / 100.0f) : ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @DesignerProperty(defaultValue = "false", editorType = "boolean")
    @SimpleProperty(description = "Set whether the dialog is cancelable or not")
    public void Cancelable(final boolean cancelable) {
        isCancelable = cancelable;
    }

    @SimpleProperty
    public boolean IsCancelable() {
        return isCancelable;
    }

    @DesignerProperty(defaultValue = "&HFF000000", editorType = "color")
    @SimpleProperty
    public void BackgroundColor(final int argb) {
        tipColor = argb;
    }

    @SimpleProperty
    public final int DialogBottom() {
        return 80;
    }

    @SimpleProperty
    public final int DialogCenter() {
        return 16;
    }

    @SimpleProperty
    public final int DialogTop() {
        return 45;
    }

    @SimpleFunction(description = "Display dialogue")
    public void Show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    @SimpleFunction(description = "Register the given component as Dialog")
    public void Register(final AndroidViewComponent component, final int height, final int width, float dimAmount, int gravity, float radiusTopLeft, float radiusTopRight, float radiusBottomLeft, float radiusBottomRight) {
        float[] radii = {
            radiusTopLeft, radiusTopLeft,
            radiusTopRight, radiusTopRight,
            radiusBottomLeft, radiusBottomLeft,
            radiusBottomRight, radiusBottomRight
        };
        View view = component.getView();
        ((ViewGroup) view.getParent()).removeView(view);

        if (dialog == null) {
            initializeDialog();
        }

        dialog.setContentView(view);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = calculate(width);
        params.height = calculate(height);
        params.dimAmount = dimAmount;
        params.gravity = gravity;
        dialog.setCancelable(isCancelable);
        dialog.getWindow().setDimAmount(dimAmount);
        dialog.getWindow().setAttributes(params);

        ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(radii, null, null));
        shapeDrawable.getPaint().setColor(tipColor);
        component.getView().setBackgroundDrawable(shapeDrawable);
    }

    @SimpleFunction(description = "To hide the dialogue or disable")
    public void Dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @SimpleFunction(description = "Unregister the component as a dialog")
    public void UnRegister() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @SimpleFunction(description = "If the dialogue is currently showing")
    public boolean IsShowing() {
        return dialog != null && dialog.isShowing();
    }

    @SimpleEvent(description = "Fired when dialog dismissed")
    public void Dismissed() {
        EventDispatcher.dispatchEvent(this, "Dismissed");
    }

    @SimpleEvent(description = "Fired when dialog is shown")
    public void Shown() {
        EventDispatcher.dispatchEvent(this, "Shown");
    }

    @Override
    public void onDismiss(final DialogInterface dialogInterface) {
        Dismissed();
    }

    @Override
    public void onShow(final DialogInterface dialogInterface) {
        Shown();
    }
}
