package cn.ningmo.huskhomesform.utils;

import org.geysermc.floodgate.api.FloodgateApi;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.form.CustomForm;
import java.util.List;
import java.util.ArrayList;
import cn.ningmo.huskhomesform.HuskHomesForm;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.response.CustomFormResponse;

public class FormUtil {
    
    public static boolean isBedrockPlayer(Player player) {
        return FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
    }
    
    public static void sendHomeListForm(Player player, List<String> homes) {
        if (player == null || homes == null) {
            HuskHomesForm.getInstance().getLogger().warning(
                HuskHomesForm.getInstance().getConfig().getString("messages.system.empty-player-or-homes")
            );
            return;
        }
        try {
            SimpleForm.Builder form = SimpleForm.builder()
                .title(HuskHomesForm.getInstance().getConfig().getString("messages.form.home-title", "我的家"))
                .content(HuskHomesForm.getInstance().getConfig().getString("messages.form.home-content", "请选择要传送的家:"));

            for (String home : homes) {
                form.button(home);
            }

            form.validResultHandler(response -> {
                String selectedHome = homes.get(response.clickedButtonId());
                player.performCommand("home " + selectedHome);
            });

            FloodgateApi.getInstance().getPlayer(player.getUniqueId()).sendForm(form.build());
        } catch (Exception e) {
            player.sendMessage(HuskHomesForm.getInstance().getConfig().getString("messages.system.form-send-failed"));
            HuskHomesForm.getInstance().getLogger().warning(
                HuskHomesForm.getInstance().getConfig().getString("messages.system.form-error")
                    .replace("{0}", e.getMessage())
            );
        }
    }
    
    public static void sendPublicHomeListForm(Player player, List<String> publicHomes) {
        if (player == null || publicHomes == null) {
            HuskHomesForm.getInstance().getLogger().warning("传入了空的玩家或公共家园列表!");
            return;
        }
        try {
            SimpleForm.Builder form = SimpleForm.builder()
                .title(HuskHomesForm.getInstance().getConfig().getString("messages.form.phome-title"))
                .content(HuskHomesForm.getInstance().getConfig().getString("messages.form.phome-content"));

            for (String publicHome : publicHomes) {
                form.button(publicHome);
            }

            form.validResultHandler(response -> {
                String selectedHome = publicHomes.get(response.clickedButtonId());
                player.performCommand("phome " + selectedHome);
            });

            FloodgateApi.getInstance().getPlayer(player.getUniqueId()).sendForm(form.build());
        } catch (Exception e) {
            player.sendMessage("§c表单发送失败，请重试!");
            HuskHomesForm.getInstance().getLogger().warning("发送表单时出错: " + e.getMessage());
        }
    }

    public static void sendWarpListForm(Player player, List<String> warps) {
        try {
            SimpleForm.Builder form = SimpleForm.builder()
                .title(HuskHomesForm.getInstance().getConfig().getString("messages.form.warp-title"))
                .content(HuskHomesForm.getInstance().getConfig().getString("messages.form.warp-content"));

            for (String warp : warps) {
                form.button(warp);
            }

            form.validResultHandler(response -> {
                String selectedWarp = warps.get(response.clickedButtonId());
                player.performCommand("warp " + selectedWarp);
            });

            FloodgateApi.getInstance().getPlayer(player.getUniqueId()).sendForm(form.build());
        } catch (Exception e) {
            player.sendMessage("§c表单发送失败，请重试!");
            HuskHomesForm.getInstance().getLogger().warning("发送表单时出错: " + e.getMessage());
        }
    }
    
    public static void sendHomeManageForm(Player player, List<String> homes) {
        if (HuskHomesForm.getInstance().getConfig().getBoolean("debug", false)) {
            HuskHomesForm.getInstance().getLogger().info(
                HuskHomesForm.getInstance().getConfig().getString("messages.debug.open-home-manage")
                    .replace("{0}", player.getName())
            );
            HuskHomesForm.getInstance().getLogger().info(
                HuskHomesForm.getInstance().getConfig().getString("messages.debug.available-homes")
                    .replace("{0}", String.join(", ", homes))
            );
        }
        try {
            SimpleForm.Builder form = SimpleForm.builder()
                .title(HuskHomesForm.getInstance().getConfig().getString("messages.form.manage-title"))
                .content(HuskHomesForm.getInstance().getConfig().getString("messages.form.manage-content"));

            // 记录按钮顺序
            List<String> buttons = new ArrayList<>();
            
            if (player.hasPermission("huskhomes.command.sethome")) {
                form.button(HuskHomesForm.getInstance().getConfig().getString("messages.form.button.sethome"));
                buttons.add("sethome");
            }
            form.button(HuskHomesForm.getInstance().getConfig().getString("messages.form.button.teleport"));
            buttons.add("teleport");
            if (player.hasPermission("huskhomes.command.delhome")) {
                form.button(HuskHomesForm.getInstance().getConfig().getString("messages.form.button.delete"));
                buttons.add("delete");
            }
            if (player.hasPermission("huskhomes.command.edithome.privacy")) {
                form.button(HuskHomesForm.getInstance().getConfig().getString("messages.form.button.set-private-to-public"));
                buttons.add("setpublic");
            }

            form.validResultHandler(response -> {
                if (response == null) return;
                String action = buttons.get(response.clickedButtonId());
                switch (action) {
                    case "sethome" -> sendSetHomeForm(player);
                    case "teleport" -> sendHomeListForm(player, homes);
                    case "delete" -> sendDeleteHomeForm(player, homes);
                    case "setpublic" -> sendSetPrivateHomeToPublicForm(player, homes);
                }
            });

            FloodgateApi.getInstance().getPlayer(player.getUniqueId()).sendForm(form.build());
        } catch (Exception e) {
            player.sendMessage(HuskHomesForm.getInstance().getConfig().getString("messages.system.form-send-failed"));
            HuskHomesForm.getInstance().getLogger().warning(
                HuskHomesForm.getInstance().getConfig().getString("messages.system.form-error")
                    .replace("{0}", e.getMessage())
            );
        }
    }

    public static void sendSetHomeForm(Player player) {
        try {
            CustomForm.Builder form = CustomForm.builder()
                .title(HuskHomesForm.getInstance().getConfig().getString("messages.form.sethome-title"))
                .input(HuskHomesForm.getInstance().getConfig().getString("messages.form.sethome-input"), 
                      HuskHomesForm.getInstance().getConfig().getString("messages.form.sethome-placeholder"));

            form.validResultHandler(response -> {
                if (response != null && response.getInput(0) != null) {
                    String homeName = response.getInput(0);
                    player.performCommand("sethome " + homeName);
                    player.sendMessage(
                        HuskHomesForm.getInstance().getConfig().getString("messages.setting-home")
                            .replace("{0}", homeName)
                    );
                }
            });

            FloodgateApi.getInstance().getPlayer(player.getUniqueId()).sendForm(form.build());
        } catch (Exception e) {
            player.sendMessage(HuskHomesForm.getInstance().getConfig().getString("messages.system.form-send-failed"));
            HuskHomesForm.getInstance().getLogger().warning(
                HuskHomesForm.getInstance().getConfig().getString("messages.system.form-error")
                    .replace("{0}", e.getMessage())
            );
        }
    }

    public static void sendDeleteHomeForm(Player player, List<String> homes) {
        try {
            SimpleForm.Builder form = SimpleForm.builder()
                .title(HuskHomesForm.getInstance().getConfig().getString("messages.form.delhome-title", "删除家园"))
                .content(HuskHomesForm.getInstance().getConfig().getString("messages.form.delhome-content", "请选择要删除的家:"));

            for (String home : homes) {
                form.button("x " + home);
            }

            form.validResultHandler(response -> {
                if (response != null) {
                    String selectedHome = homes.get(response.clickedButtonId());
                    player.performCommand("delhome " + selectedHome);
                }
            });

            FloodgateApi.getInstance().getPlayer(player.getUniqueId()).sendForm(form.build());
        } catch (Exception e) {
            player.sendMessage("§c表单发送失败，请重试!");
            HuskHomesForm.getInstance().getLogger().warning("发送表单时出错: " + e.getMessage());
        }
    }
    
    public static void sendPublicHomeManageForm(Player player, List<String> publicHomes) {
        if (HuskHomesForm.getInstance().getConfig().getBoolean("debug", false)) {
            HuskHomesForm.getInstance().getLogger().info("为玩家 " + player.getName() + " 打开公共家园管理表单");
            HuskHomesForm.getInstance().getLogger().info("可用的公共家园: " + String.join(", ", publicHomes));
        }
        try {
            SimpleForm.Builder form = SimpleForm.builder()
                .title(HuskHomesForm.getInstance().getConfig().getString("messages.form.phome-manage-title"))
                .content(HuskHomesForm.getInstance().getConfig().getString("messages.form.phome-manage-content"));

            // 记录按钮顺序
            List<String> buttons = new ArrayList<>();
            
            if (player.hasPermission("huskhomes.command.setphome")) {
                form.button(HuskHomesForm.getInstance().getConfig().getString("messages.form.button.setphome"));
                buttons.add("setphome");
            }
            form.button(HuskHomesForm.getInstance().getConfig().getString("messages.form.button.teleport"));
            buttons.add("teleport");
            if (player.hasPermission("huskhomes.command.delphome")) {
                form.button(HuskHomesForm.getInstance().getConfig().getString("messages.form.button.delete"));
                buttons.add("delete");
            }

            form.validResultHandler(response -> {
                if (response == null) return;
                String action = buttons.get(response.clickedButtonId());
                switch (action) {
                    case "setphome" -> sendSetPublicHomeForm(player);
                    case "teleport" -> sendPublicHomeListForm(player, publicHomes);
                    case "delete" -> sendDeletePublicHomeForm(player, publicHomes);
                }
            });

            FloodgateApi.getInstance().getPlayer(player.getUniqueId()).sendForm(form.build());
        } catch (Exception e) {
            player.sendMessage("§c表单发送失败，请重试!");
            HuskHomesForm.getInstance().getLogger().warning("发送表单时出错: " + e.getMessage());
        }
    }

    public static void sendSetPublicHomeForm(Player player) {
        try {
            CustomForm.Builder form = CustomForm.builder()
                .title(HuskHomesForm.getInstance().getConfig().getString("messages.form.setphome-title"))
                .input(HuskHomesForm.getInstance().getConfig().getString("messages.form.setphome-input"), 
                      HuskHomesForm.getInstance().getConfig().getString("messages.form.setphome-placeholder"));

            form.validResultHandler(response -> {
                if (response != null && response.getInput(0) != null) {
                    String homeName = response.getInput(0);
                    player.performCommand("setphome " + homeName);
                    player.sendMessage("§a正在设置公共家: " + homeName);
                }
            });

            FloodgateApi.getInstance().getPlayer(player.getUniqueId()).sendForm(form.build());
        } catch (Exception e) {
            player.sendMessage("§c表单发送失败，请重试!");
            HuskHomesForm.getInstance().getLogger().warning("发送表单时出错: " + e.getMessage());
        }
    }

    public static void sendDeletePublicHomeForm(Player player, List<String> publicHomes) {
        try {
            SimpleForm.Builder form = SimpleForm.builder()
                .title(HuskHomesForm.getInstance().getConfig().getString("messages.form.delphome-title", "删除公共家"))
                .content(HuskHomesForm.getInstance().getConfig().getString("messages.form.delphome-content", "请选择要删除的公共家:"));

            for (String home : publicHomes) {
                form.button("x " + home);
            }

            form.validResultHandler(response -> {
                if (response != null) {
                    String selectedHome = publicHomes.get(response.clickedButtonId());
                    player.performCommand("delphome " + selectedHome);
                }
            });

            FloodgateApi.getInstance().getPlayer(player.getUniqueId()).sendForm(form.build());
        } catch (Exception e) {
            player.sendMessage("§c表单发送失败，请重试!");
            HuskHomesForm.getInstance().getLogger().warning("发送表单时出错: " + e.getMessage());
        }
    }

    public static void sendWarpManageForm(Player player, List<String> warps) {
        if (HuskHomesForm.getInstance().getConfig().getBoolean("debug", false)) {
            HuskHomesForm.getInstance().getLogger().info("为玩家 " + player.getName() + " 打开传送点管理表单");
            HuskHomesForm.getInstance().getLogger().info("可用的传送点: " + String.join(", ", warps));
        }
        try {
            SimpleForm.Builder form = SimpleForm.builder()
                .title(HuskHomesForm.getInstance().getConfig().getString("messages.form.warp-manage-title"))
                .content(HuskHomesForm.getInstance().getConfig().getString("messages.form.warp-manage-content"));

            // 记录按钮顺序
            List<String> buttons = new ArrayList<>();
            
            if (player.hasPermission("huskhomes.command.setwarp")) {
                form.button(HuskHomesForm.getInstance().getConfig().getString("messages.form.button.setwarp"));
                buttons.add("setwarp");
            }
            form.button(HuskHomesForm.getInstance().getConfig().getString("messages.form.button.teleport-warp"));
            buttons.add("teleport");
            if (player.hasPermission("huskhomes.command.delwarp")) {
                form.button(HuskHomesForm.getInstance().getConfig().getString("messages.form.button.delete"));
                buttons.add("delete");
            }

            form.validResultHandler(response -> {
                if (response == null) return;
                String action = buttons.get(response.clickedButtonId());
                switch (action) {
                    case "setwarp" -> sendSetWarpForm(player);
                    case "teleport" -> sendWarpListForm(player, warps);
                    case "delete" -> sendDeleteWarpForm(player, warps);
                }
            });

            FloodgateApi.getInstance().getPlayer(player.getUniqueId()).sendForm(form.build());
        } catch (Exception e) {
            player.sendMessage("§c表单发送失败，请重试!");
            HuskHomesForm.getInstance().getLogger().warning("发送表单时出错: " + e.getMessage());
        }
    }

    public static void sendSetWarpForm(Player player) {
        try {
            CustomForm.Builder form = CustomForm.builder()
                .title(HuskHomesForm.getInstance().getConfig().getString("messages.form.setwarp-title"))
                .input(HuskHomesForm.getInstance().getConfig().getString("messages.form.setwarp-input"), 
                      HuskHomesForm.getInstance().getConfig().getString("messages.form.setwarp-placeholder"));

            form.validResultHandler(response -> {
                if (response != null && response.getInput(0) != null) {
                    String warpName = response.getInput(0);
                    player.performCommand("setwarp " + warpName);
                    player.sendMessage("§a正在设置传送点: " + warpName);
                }
            });

            FloodgateApi.getInstance().getPlayer(player.getUniqueId()).sendForm(form.build());
        } catch (Exception e) {
            player.sendMessage("§c表单发送失败，请重试!");
            HuskHomesForm.getInstance().getLogger().warning("发送表单时出错: " + e.getMessage());
        }
    }

    public static void sendDeleteWarpForm(Player player, List<String> warps) {
        try {
            SimpleForm.Builder form = SimpleForm.builder()
                .title(HuskHomesForm.getInstance().getConfig().getString("messages.form.delwarp-title", "删除传送点"))
                .content(HuskHomesForm.getInstance().getConfig().getString("messages.form.delwarp-content", "请选择要删除的传送点:"));

            for (String warp : warps) {
                form.button("x " + warp);
            }

            form.validResultHandler(response -> {
                if (response != null) {
                    String selectedWarp = warps.get(response.clickedButtonId());
                    player.performCommand("delwarp " + selectedWarp);
                }
            });

            FloodgateApi.getInstance().getPlayer(player.getUniqueId()).sendForm(form.build());
        } catch (Exception e) {
            player.sendMessage("§c表单发送失败，请重试!");
            HuskHomesForm.getInstance().getLogger().warning("发送表单时出错: " + e.getMessage());
        }
    }
    
    public static void sendSetPrivateHomeToPublicForm(Player player, List<String> homes) {
        try {
            SimpleForm.Builder form = SimpleForm.builder()
                .title(HuskHomesForm.getInstance().getConfig().getString("messages.form.set-private-to-public-title"))
                .content(HuskHomesForm.getInstance().getConfig().getString("messages.form.set-private-to-public-content"));

            for (String home : homes) {
                form.button(home);
            }

            form.validResultHandler(response -> {
                if (response != null) {
                    String selectedHome = homes.get(response.clickedButtonId());
                    // 使用 edithome 命令设置隐私状态
                    player.performCommand("edithome " + selectedHome + " privacy public");
                    player.sendMessage(
                        HuskHomesForm.getInstance().getConfig().getString("messages.system.home-set-to-public")
                            .replace("{0}", selectedHome)
                    );
                }
            });

            FloodgateApi.getInstance().getPlayer(player.getUniqueId()).sendForm(form.build());
        } catch (Exception e) {
            player.sendMessage(HuskHomesForm.getInstance().getConfig().getString("messages.system.form-send-failed"));
            HuskHomesForm.getInstance().getLogger().warning(
                HuskHomesForm.getInstance().getConfig().getString("messages.debug.form-error")
                    .replace("{0}", e.getMessage())
            );
        }
    }
    
    // 其他表单方法...
} 