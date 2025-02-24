package cn.ningmo.huskhomesform;

import org.bukkit.plugin.java.JavaPlugin;
import net.william278.huskhomes.api.HuskHomesAPI;
import cn.ningmo.huskhomesform.commands.HomeFormCommand;
import cn.ningmo.huskhomesform.commands.PublicHomeFormCommand;
import cn.ningmo.huskhomesform.commands.WarpFormCommand;
import org.bukkit.plugin.Plugin;

public class HuskHomesForm extends JavaPlugin {
    private static HuskHomesForm instance;
    private HuskHomesAPI huskHomesAPI;

    @Override
    public void onEnable() {
        instance = this;
        
        // 保存默认配置
        saveDefaultConfig();
        
        // 检查配置文件完整性
        checkConfig();
        
        // 获取HuskHomes API实例
        huskHomesAPI = HuskHomesAPI.getInstance();
        
        // 注册命令
        getCommand("homef").setExecutor(new HomeFormCommand());
        getCommand("phomef").setExecutor(new PublicHomeFormCommand());
        getCommand("warpf").setExecutor(new WarpFormCommand());
        
        // 检查依赖插件版本
        Plugin huskHomes = getServer().getPluginManager().getPlugin("HuskHomes");
        if (huskHomes == null) {
            getLogger().severe("未找到 HuskHomes 插件!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        String version = huskHomes.getDescription().getVersion();
        if (!version.startsWith("4.")) {
            getLogger().warning("HuskHomes 版本可能不兼容! 当前版本: " + version);
        }
        
        getLogger().info("HuskHomesForm 已成功启用!");
    }

    @Override
    public void onDisable() {
        getLogger().info("HuskHomesForm 已禁用!");
    }

    public static HuskHomesForm getInstance() {
        return instance;
    }

    public HuskHomesAPI getHuskHomesAPI() {
        return huskHomesAPI;
    }

    private void checkConfig() {
        // 检查必要的配置项
        String[] requiredMessages = {
            "messages.no-homes",
            "messages.no-public-homes",
            "messages.no-warps",
            "messages.form.home-title",
            "messages.form.home-content",
            "messages.form.phome-title",
            "messages.form.phome-content",
            "messages.form.warp-title",
            "messages.form.warp-content",
            "messages.form.manage-title",
            "messages.form.manage-content",
            "messages.form.sethome-title",
            "messages.form.sethome-input",
            "messages.form.sethome-placeholder",
            "messages.form.delhome-title",
            "messages.form.delhome-content",
            "messages.form.phome-manage-title",
            "messages.form.phome-manage-content",
            "messages.form.setphome-title",
            "messages.form.setphome-input",
            "messages.form.setphome-placeholder",
            "messages.form.delphome-title",
            "messages.form.delphome-content",
            "messages.form.warp-manage-title",
            "messages.form.warp-manage-content",
            "messages.form.setwarp-title",
            "messages.form.setwarp-input",
            "messages.form.setwarp-placeholder",
            "messages.form.delwarp-title",
            "messages.form.delwarp-content",
            "messages.form.set-private-to-public-title",
            "messages.form.set-private-to-public-content",
            "messages.form.button.sethome",
            "messages.form.button.setphome",
            "messages.form.button.setwarp",
            "messages.form.button.teleport",
            "messages.form.button.teleport-warp",
            "messages.form.button.delete",
            "messages.form.button.set-private-to-public",
            "messages.system.player-only",
            "messages.system.no-permission",
            "messages.system.form-send-failed",
            "messages.system.setting-home",
            "messages.system.setting-public-home",
            "messages.system.setting-warp",
            "messages.system.empty-player-or-homes",
            "messages.system.empty-player-or-public-homes",
            "messages.system.empty-player-or-warps",
            "messages.debug.open-home-manage",
            "messages.debug.available-homes",
            "messages.debug.open-public-home-manage",
            "messages.debug.available-public-homes",
            "messages.debug.open-warp-manage",
            "messages.debug.available-warps",
            "messages.debug.form-error"
        };
        
        for (String path : requiredMessages) {
            if (!getConfig().contains(path)) {
                getLogger().warning("配置文件中缺少 " + path + " 项!");
            }
        }
    }
} 