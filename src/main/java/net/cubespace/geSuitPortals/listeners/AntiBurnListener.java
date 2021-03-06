package net.cubespace.geSuitPortals.listeners;

import net.cubespace.geSuitPortals.managers.PortalsManager;
import net.cubespace.geSuitPortals.objects.Portal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class AntiBurnListener implements Listener {
    private static final int FIRE_SPREAD_RADIUS = 2;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamageEvent(EntityDamageEvent event) {
        if (!PortalsManager.RECEIVED || !(event.getEntity() instanceof Player) ||
           (!event.getCause().equals(EntityDamageEvent.DamageCause.LAVA) &&
            !event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK) &&
            !event.getCause().equals(EntityDamageEvent.DamageCause.FIRE))) return;

        Location location = event.getEntity().getLocation();
        event.setCancelled(check(event.getEntity(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), FIRE_SPREAD_RADIUS));
    }

    private boolean check(Entity entity, int x, int y, int z, int radius) {
        return  checkIfInPortal(entity.getWorld().getBlockAt(x, y, z)) ||
                (radius > 0 && (check(entity, x + 1, y, z, radius - 1)
                            ||  check(entity, x - 1, y, z, radius - 1)
                            ||  check(entity, x, y + 1, z, radius - 1)
                            ||  check(entity, x, y - 1, z, radius - 1)
                            ||  check(entity, x, y, z + 1, radius - 1)
                            ||  check(entity, x, y, z - 1, radius - 1)));
    }

    private boolean checkIfInPortal(Block block) {
        if (canIgnite(block)) {
            for (Portal p : PortalsManager.PORTALS.get(block.getWorld())) {
                if (p.isBlockInPortal(block)) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    private boolean canIgnite(Block block) {
        Material mat = block.getType();

        return (mat == Material.LAVA || mat == Material.STATIONARY_LAVA);
    }
}
