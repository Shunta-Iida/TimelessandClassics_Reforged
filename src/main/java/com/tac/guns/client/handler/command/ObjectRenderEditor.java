package com.tac.guns.client.handler.command;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.apache.logging.log4j.Level;
import org.lwjgl.glfw.GLFW;

import com.google.gson.GsonBuilder;
import com.tac.guns.Config;
import com.tac.guns.GunMod;
import com.tac.guns.client.Keys;
import com.tac.guns.common.Gun;
import com.tac.guns.common.tooling.CommandsHandler;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ObjectRenderEditor {
    private static ObjectRenderEditor instance;

    public static ObjectRenderEditor get() {
        if (ObjectRenderEditor.instance == null) {
            ObjectRenderEditor.instance = new ObjectRenderEditor();
        }
        return ObjectRenderEditor.instance;
    }

    private ObjectRenderEditor() {}

    public int currElement = 0;
    private final HashMap<Integer, RENDER_Element> elements = new HashMap<>();

    public RENDER_Element GetFromElements(final int index) {
        return this.elements.get(index);
    }

    public static class RENDER_Element {
        private float xMod = 0;
        private float yMod = 0;
        private float zMod = 0;
        private float sizeMod = 0;

        public RENDER_Element(final float x, final float y, final float z, final float sizeX) {
            this.xMod = x;
            this.zMod = z;
            this.yMod = y;
            this.sizeMod = sizeX;
        }

        public float getxMod() {
            return this.xMod;
        }

        public float getyMod() {
            return this.yMod;
        }

        public float getzMod() {
            return this.zMod;
        }

        public float getSizeMod() {
            return this.sizeMod;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onKeyPressed(final InputEvent.KeyInputEvent event) {
        // Basics overview
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
            return;
        if (!Config.COMMON.development.enableTDev.get()
                && CommandsHandler.get().getCatCurrentIndex() == 4)
            return;
        if (event.getKey() == GLFW.GLFW_KEY_1) {
            this.currElement = 1;
            // event.setCanceled(true);
            return;
        } else if (event.getKey() == GLFW.GLFW_KEY_2) {
            this.currElement = 2;
            // event.setCanceled(true);
            return;
        } else if (event.getKey() == GLFW.GLFW_KEY_3) {
            this.currElement = 3;
            // event.setCanceled(true);
            return;
        } else if (event.getKey() == GLFW.GLFW_KEY_4) {
            this.currElement = 4;
            // event.setCanceled(true);
            return;
        } else if (event.getKey() == GLFW.GLFW_KEY_5) {
            this.currElement = 5;
            // event.setCanceled(true);
            return;
        } else if (event.getKey() == GLFW.GLFW_KEY_6) {
            this.currElement = 6;
            // event.setCanceled(true);
            return;
        } else if (event.getKey() == GLFW.GLFW_KEY_7) {
            this.currElement = 7;
            // event.setCanceled(true);
            return;
        } else if (event.getKey() == GLFW.GLFW_KEY_8) {
            this.currElement = 8;
            // event.setCanceled(true);
            return;
        } else if (event.getKey() == GLFW.GLFW_KEY_9) {
            this.currElement = 9;
            // event.setCanceled(true);
            return;
        }

        final boolean isLeft = event.getKey() == GLFW.GLFW_KEY_LEFT;
        final boolean isRight = event.getKey() == GLFW.GLFW_KEY_RIGHT;
        final boolean isUp = event.getKey() == GLFW.GLFW_KEY_UP;
        final boolean isDown = event.getKey() == GLFW.GLFW_KEY_DOWN;

        final boolean isControlDown = Keys.CONTROLLY.isDown() || Keys.CONTROLLYR.isDown(); // Increase Module Size
        final boolean isShiftDown = event.getKey() == GLFW.GLFW_KEY_LEFT_SHIFT; // Increase Step Size
        final boolean isAltDown = Keys.ALTY.isDown() || Keys.ALTYR.isDown(); // Swap X -> Z modify
        final boolean isPeriodDown = Keys.SIZE_OPT.isDown();

        final RENDER_Element element =
                this.elements.size() == 0 || !this.elements.containsKey(this.currElement)
                        ? new RENDER_Element(0, 0, 0, 0)
                        : this.elements.get(this.currElement);
        float xMod = element.xMod;
        float yMod = element.yMod;
        float zMod = element.zMod;
        float sizeMod = element.sizeMod;
        float stepModifier = 1;
        /*
         * if(isShiftDown)
         * stepModifier*=10;
         */
        if (isShiftDown)
            stepModifier /= 10;

        if (isPeriodDown && isUp) {
            sizeMod += 0.05 * stepModifier;
        } else if (isPeriodDown && isDown) {
            sizeMod -= 0.05 * stepModifier;
        } else if (isControlDown && isUp) {
            zMod += 0.05 * stepModifier;
        } else if (isControlDown && isDown) {
            zMod -= 0.05 * stepModifier;
        } else if (isLeft) {
            xMod -= 0.05 * stepModifier;
        } else if (isRight) {
            xMod += 0.05 * stepModifier;
        } else if (isUp) {
            yMod += 0.05 * stepModifier;
        } else if (isDown) {
            yMod -= 0.05 * stepModifier;
        }

        this.elements.put(this.currElement, new RENDER_Element(xMod, yMod, zMod, sizeMod));
    }

    public void exportData() {
        this.elements.forEach((name, data) -> {
            if (this.elements.get(name) == null) {
                GunMod.LOGGER.log(Level.ERROR,
                        "OBJ_RENDER EDITOR FAILED TO EXPORT THIS BROKEN DATA. CONTACT CLUMSYALIEN.");
                return;
            }
            final GsonBuilder gsonB = new GsonBuilder().setLenient()
                    .addSerializationExclusionStrategy(Gun.strategy).setPrettyPrinting();

            final String jsonString = gsonB.create().toJson(data);
            this.writeExport(jsonString, "OBJ_RENDER" + name);
        });
    }

    private void writeExport(final String jsonString, final String name) {
        try {
            final File dir = new File(Config.COMMON.development.TDevPath.get() + "\\tac_export\\");
            dir.mkdir();
            final FileWriter dataWriter =
                    new FileWriter(dir.getAbsolutePath() + "\\" + name + "_export.json");
            dataWriter.write(jsonString);
            dataWriter.close();
            GunMod.LOGGER.log(Level.INFO,
                    "OBJ_RENDER EDITOR EXPORTED FILE ( " + name + "export.txt ). BE PROUD!");
        } catch (final IOException e) {
            GunMod.LOGGER.log(Level.ERROR,
                    "OBJ_RENDER EDITOR FAILED TO EXPORT, NO FILE CREATED!!! NO ACCESS IN PATH?. CONTACT CLUMSYALIEN.");
        }
    }
}
