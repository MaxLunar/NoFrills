package nofrills.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static nofrills.Main.Config;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @Shadow
    private float equipProgressMainHand;

    @Shadow
    private float equipProgressOffHand;

    @Shadow
    private float lastEquipProgressMainHand;

    @Shadow
    private float lastEquipProgressOffHand;

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", shift = At.Shift.AFTER))
    private void onBeforeRenderItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (Config.viewmodelEnable()) {
            if (hand == Hand.MAIN_HAND) {
                matrices.translate(Config.viewmodelOffsetX(), Config.viewmodelOffsetY(), Config.viewmodelOffsetZ());
            } else {
                matrices.translate(-Config.viewmodelOffsetX(), Config.viewmodelOffsetY(), Config.viewmodelOffsetZ());
            }
        }
    }

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    private void onRenderItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (Config.viewmodelEnable()) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Config.viewmodelRotX()));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(Config.viewmodelRotY()));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(Config.viewmodelRotZ()));
            matrices.scale(Config.viewmodelScaleX(), Config.viewmodelScaleY(), Config.viewmodelScaleZ());
        }
    }

    @Inject(method = "renderArmHoldingItem", at = @At("HEAD"))
    private void onBeforeRenderHand(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, Arm arm, CallbackInfo ci) {
        if (Config.viewmodelEnable()) {
            if (arm == Arm.RIGHT) {
                matrices.translate(Config.viewmodelOffsetX(), Config.viewmodelOffsetY(), Config.viewmodelOffsetZ());
            } else {
                matrices.translate(-Config.viewmodelOffsetX(), Config.viewmodelOffsetY(), Config.viewmodelOffsetZ());
            }
        }
    }

    @Inject(method = "renderArmHoldingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;getRenderer(Lnet/minecraft/entity/Entity;)Lnet/minecraft/client/render/entity/EntityRenderer;"))
    private void onRenderHand(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, Arm arm, CallbackInfo ci) {
        if (Config.viewmodelEnable()) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Config.viewmodelRotX()));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(Config.viewmodelRotY()));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(Config.viewmodelRotZ()));
            matrices.scale(Config.viewmodelScaleX(), Config.viewmodelScaleY(), Config.viewmodelScaleZ());
        }
    }

    @Inject(method = "shouldSkipHandAnimationOnSwap", at = @At("HEAD"), cancellable = true)
    private void onShouldSkipAnimation(ItemStack from, ItemStack _to, CallbackInfoReturnable<Boolean> cir) {
        if (Config.noEquipAnim()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "updateHeldItems", at = @At("TAIL"))
    private void onUpdateHeldItems(CallbackInfo ci) {
        if (Config.noEquipAnim()) {
            this.equipProgressMainHand = 1.0f;
            this.equipProgressOffHand = 1.0f;
            this.lastEquipProgressMainHand = 1.0f;
            this.lastEquipProgressOffHand = 1.0f;
        }
    }
}
