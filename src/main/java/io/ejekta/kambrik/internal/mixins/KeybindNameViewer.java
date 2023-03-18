package io.ejekta.kambrik.internal.mixins;

import io.ejekta.kambrik.internal.input.InputLabelHandler;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ControlsListWidget.KeyBindingEntry.class)
public abstract class KeybindNameViewer {

    @Redirect(method = "<init>(Lnet/minecraft/client/gui/screen/option/ControlsListWidget;Lnet/minecraft/client/option/KeyBinding;Lnet/minecraft/text/Text;)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/gui/screen/option/ControlsListWidget$KeyBindingEntry;bindingName:Lnet/minecraft/text/Text;",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void bo_handleChangingTextName(ControlsListWidget.KeyBindingEntry instance, Text value) {
        InputLabelHandler.INSTANCE.handle(instance, value);
    }
}
