package dev.cfstudio.vanta;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

public final class VantaScreen extends Screen {
    private static final long OPEN_DURATION = 260L;
    private static final long CLOSE_DURATION = 190L;

    private FeatureRegistry.Category selected = FeatureRegistry.Category.VISUALS;
    private long animationStart = Util.getMillis();
    private boolean closing;

    public VantaScreen() {
        super(Component.literal("Vanta Client"));
    }

    public void beginClose() {
        if (!closing) {
            closing = true;
            animationStart = Util.getMillis();
        }
    }

    @Override
    public void onClose() {
        beginClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private float progress() {
        long elapsed = Util.getMillis() - animationStart;
        float raw = Mth.clamp(elapsed / (float) (closing ? CLOSE_DURATION : OPEN_DURATION), 0.0f, 1.0f);
        float eased = 1.0f - (float) Math.pow(1.0f - raw, 3.0);
        return closing ? 1.0f - eased : eased;
    }

    private int panelWidth() {
        return Math.min(720, Math.max(300, width - 24));
    }

    private int panelHeight() {
        return Math.min(420, Math.max(270, height - 20));
    }

    private int panelX() {
        return (width - panelWidth()) / 2;
    }

    private int panelY() {
        return (height - panelHeight()) / 2 + (int) ((1.0f - progress()) * 15.0f);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        float animation = progress();
        if (closing && animation <= 0.0f) {
            Minecraft.getInstance().setScreen(null);
            return;
        }

        int x = panelX();
        int y = panelY();
        int w = panelWidth();
        int h = panelHeight();

        g.fill(0, 0, width, height, 0x99030812);
        g.fillGradient(x, y, x + w, y + h, 0xF00D1626, 0xF0131022);
        g.fillGradient(x, y, x + w, y + 3, 0xFF55D7FF, 0xFFAA78FF);
        g.fill(x, y + 3, x + w, y + 4, 0x663F7899);

        draw(g, "VANTA", x + 22, y + 17, 0xFFF2F7FF);
        draw(g, "CLIENT  /  1.21.11", x + 22, y + 34, 0xFF7E9CB8);
        draw(g, FeatureRegistry.enabledCount() + " ACTIVE", x + w - 106, y + 23, 0xFF6CE8C2);

        int closeX = x + w - 30;
        int closeY = y + 12;
        boolean closeHover = inside(mouseX, mouseY, closeX, closeY, 18, 18);
        g.fill(closeX, closeY, closeX + 18, closeY + 18, closeHover ? 0xFF75364A : 0xFF2A2939);
        draw(g, "×", closeX + 5, closeY + 2, 0xFFFFA7BA);

        int sidebarX = x + 12;
        int sidebarY = y + 66;
        int sidebarW = 128;
        int sidebarH = h - 78;
        g.fillGradient(sidebarX, sidebarY, sidebarX + sidebarW, sidebarY + sidebarH, 0xAA101C2B, 0xAA171323);

        draw(g, "MODULES", sidebarX + 12, sidebarY + 12, 0xFF6E8DAA);
        int tabY = sidebarY + 35;
        for (FeatureRegistry.Category category : FeatureRegistry.Category.values()) {
            boolean active = selected == category;
            int tabHeight = 34;
            int color = active ? 0xFF243B53 : 0x00101828;
            g.fill(sidebarX + 7, tabY, sidebarX + sidebarW - 7, tabY + tabHeight, color);
            if (active) {
                g.fillGradient(sidebarX + 7, tabY, sidebarX + 10, tabY + tabHeight, 0xFF55D7FF, 0xFFB58AFF);
            }
            draw(g, category.title(), sidebarX + 17, tabY + 12, active ? 0xFFF3F8FF : 0xFF91A4BA);
            tabY += 39;
        }

        int contentX = x + 156;
        int contentY = y + 76;
        draw(g, selected.title().toUpperCase() + " / MODULES", contentX, contentY, 0xFFB9D0E8);
        draw(g, "CLIENT-SIDE FEATURES", x + w - 180, contentY, 0xFF71879F);

        var modules = FeatureRegistry.inCategory(selected);
        int gap = 10;
        int cardsWidth = w - 174;
        int cardWidth = Math.max(70, (cardsWidth - gap) / 2);
        int cardHeight = 72;
        for (int i = 0; i < modules.size(); i++) {
            FeatureRegistry.Module module = modules.get(i);
            int column = i % 2;
            int row = i / 2;
            int cardX = contentX + column * (cardWidth + gap);
            int cardY = contentY + 24 + row * (cardHeight + 9);
            boolean hover = inside(mouseX, mouseY, cardX, cardY, cardWidth, cardHeight);
            boolean enabled = FeatureRegistry.isEnabled(module.id());

            g.fillGradient(cardX, cardY, cardX + cardWidth, cardY + cardHeight,
                    hover ? 0xFF1E3046 : 0xFF172336,
                    enabled ? 0xFF24213B : 0xFF1A192B);
            g.fill(cardX, cardY, cardX + 2, cardY + cardHeight, module.accent());
            draw(g, module.title(), cardX + 11, cardY + 10, 0xFFF1F5FF);
            draw(g, module.description(), cardX + 11, cardY + 29, 0xFF93A6BA);
            draw(g, "KEY  " + module.hotkey(), cardX + 11, cardY + 51, 0xFF6F879F);

            int pillX = cardX + cardWidth - 49;
            int pillY = cardY + 9;
            g.fill(pillX, pillY, pillX + 38, pillY + 17, enabled ? 0xFF176C70 : 0xFF344052);
            draw(g, enabled ? "ON" : "OFF", pillX + 8, pillY + 5,
                    enabled ? 0xFF8FFFF0 : 0xFF9DADBF);
        }

        int footerY = y + h - 27;
        draw(g, "RIGHT SHIFT  ·  OPEN/CLOSE", x + 18, footerY, 0xFF6C8298);
        draw(g, "VISUALS ARE LIMITED TO CLIENT-RECEIVED DATA", x + w - 270, footerY, 0xFF667E94);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || closing) {
            return false;
        }

        int x = panelX();
        int y = panelY();
        int w = panelWidth();
        int h = panelHeight();

        if (inside(mouseX, mouseY, x + w - 30, y + 12, 18, 18)) {
            beginClose();
            return true;
        }

        int sidebarX = x + 12;
        int sidebarY = y + 66;
        int tabY = sidebarY + 35;
        for (FeatureRegistry.Category category : FeatureRegistry.Category.values()) {
            if (inside(mouseX, mouseY, sidebarX + 7, tabY, 114, 34)) {
                selected = category;
                return true;
            }
            tabY += 39;
        }

        int contentX = x + 156;
        int contentY = y + 76;
        int gap = 10;
        int cardsWidth = w - 174;
        int cardWidth = Math.max(70, (cardsWidth - gap) / 2);
        int cardHeight = 72;
        var modules = FeatureRegistry.inCategory(selected);
        for (int i = 0; i < modules.size(); i++) {
            int column = i % 2;
            int row = i / 2;
            int cardX = contentX + column * (cardWidth + gap);
            int cardY = contentY + 24 + row * (cardHeight + 9);
            if (inside(mouseX, mouseY, cardX, cardY, cardWidth, cardHeight)) {
                FeatureRegistry.toggle(modules.get(i).id());
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private static boolean inside(double mouseX, double mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
    }

    private static void draw(GuiGraphics graphics, String text, int x, int y, int color) {
        graphics.drawString(Minecraft.getInstance().font, text, x, y, color, false);
    }
}
