package cn.ningmo.huskhomesform.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import cn.ningmo.huskhomesform.HuskHomesForm;
import cn.ningmo.huskhomesform.utils.FormUtil;
import java.util.List;
import java.util.stream.Collectors;
import net.william278.huskhomes.position.Home;
import net.william278.huskhomes.user.OnlineUser;
import net.william278.huskhomes.user.BukkitUser;
import net.william278.huskhomes.BukkitHuskHomes;
import org.bukkit.Bukkit;

public class HomeFormCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(HuskHomesForm.getInstance().getConfig().getString("messages.player-only"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("huskhomesform.use")) {
            player.sendMessage(HuskHomesForm.getInstance().getConfig().getString("messages.no-permission"));
            return true;
        }
        
        if (!FormUtil.isBedrockPlayer(player)) {
            player.performCommand("home");
            return true;
        }
        
        BukkitHuskHomes plugin = (BukkitHuskHomes) Bukkit.getPluginManager().getPlugin("HuskHomes");
        OnlineUser user = BukkitUser.adapt(player, plugin);
        HuskHomesForm.getInstance().getHuskHomesAPI().getUserHomes(user).thenAccept(homes -> {
            List<String> homeNames = homes.stream()
                .map(Home::getName)
                .collect(Collectors.toList());
            
            if (homeNames.isEmpty()) {
                player.sendMessage(HuskHomesForm.getInstance().getConfig().getString("messages.no-homes"));
                FormUtil.sendSetHomeForm(player);
                return;
            }
            
            FormUtil.sendHomeManageForm(player, homeNames);
        });
        
        return true;
    }
} 