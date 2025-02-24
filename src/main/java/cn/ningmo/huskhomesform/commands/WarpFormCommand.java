package cn.ningmo.huskhomesform.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import cn.ningmo.huskhomesform.HuskHomesForm;
import cn.ningmo.huskhomesform.utils.FormUtil;
import java.util.List;
import java.util.stream.Collectors;
import net.william278.huskhomes.position.Warp;
import net.william278.huskhomes.user.OnlineUser;
import net.william278.huskhomes.user.BukkitUser;
import net.william278.huskhomes.BukkitHuskHomes;
import org.bukkit.Bukkit;

public class WarpFormCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(HuskHomesForm.getInstance().getConfig().getString("messages.system.player-only"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("huskhomesform.use")) {
            player.sendMessage(HuskHomesForm.getInstance().getConfig().getString("messages.system.no-permission"));
            return true;
        }
        
        if (!FormUtil.isBedrockPlayer(player)) {
            player.performCommand("warp");
            return true;
        }
        
        BukkitHuskHomes plugin = (BukkitHuskHomes) Bukkit.getPluginManager().getPlugin("HuskHomes");
        OnlineUser user = BukkitUser.adapt(player, plugin);
        HuskHomesForm.getInstance().getHuskHomesAPI().getWarps().thenAccept(warps -> {
            List<String> warpNames = warps.stream()
                .map(Warp::getName)
                .collect(Collectors.toList());
            
            if (warpNames.isEmpty()) {
                player.sendMessage(HuskHomesForm.getInstance().getConfig().getString("messages.no-warps"));
                if (player.hasPermission("huskhomes.command.setwarp")) {
                    FormUtil.sendSetWarpForm(player);
                }
                return;
            }
            
            FormUtil.sendWarpManageForm(player, warpNames);
        });
        
        return true;
    }
} 