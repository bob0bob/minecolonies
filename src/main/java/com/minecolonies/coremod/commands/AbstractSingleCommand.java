package com.minecolonies.coremod.commands;

import com.minecolonies.api.IAPI;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.permissions.Rank;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.configuration.Configurations;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.jetbrains.annotations.NotNull;

/**
 * A command that has children. Is a single one-word command.
 */
public abstract class AbstractSingleCommand implements ISubCommand
{

    public static final String NOT_PERMITTED = "You are not allowed to do that!";
    private final String[] parents;

    /**
     * Initialize this SubCommand with it's parents.
     *
     * @param parents an array of all the parents.
     */
    public AbstractSingleCommand(@NotNull final String... parents)
    {
        this.parents = parents.clone();
    }

    /**
     * Get the ith argument (An Integer).
     *
     * @param i    the argument from the list you want.
     * @param args the list of arguments.
     * @param def  the default value.
     * @return the argument.
     */
    public static int getIthArgument(final String[] args, final int i, final int def)
    {
        if (args.length <= i)
        {
            return def;
        }

        try
        {
            return Integer.parseInt(args[i]);
        }
        catch (final NumberFormatException e)
        {
            return def;
        }
    }

    /**
     * Get the ith argument (An Integer).
     *
     * @param i    the argument from the list you want.
     * @param args the list of arguments.
     * @param def  the default value.
     * @return the argument.
     */
    public static IToken getIthArgument(final String[] args, final int i, final IToken def)
    {
        if (args.length <= i)
        {
            return def;
        }

        try
        {
            return Integer.parseInt(args[i]);
        }
        catch (final NumberFormatException e)
        {
            return def;
        }
    }

    @NotNull
    @Override
    public String getCommandUsage(@NotNull final ICommandSender sender)
    {
        final StringBuilder sb = new StringBuilder().append('/');
        for (final String parent : parents)
        {
            sb.append(parent).append(' ');
        }
        return sb.toString();
    }

    /**
     * Will check the config file to see if players are allowed to use the command that is sent here.
     * and will verify that they are of correct rank to do so.
     *
     * @param player     the players/senders name.
     * @param theCommand which command to check if the player can use it.
     * @param colonyId   the id of the colony.
     * @return boolean.
     */

    public boolean canPlayerUseCommand(final EntityPlayer player, final Commands theCommand, final IToken colonyId)
    {
        if (isPlayerOpped(player, theCommand.toString()))
        {
            return true;
        }

        final IColony chkColony = IAPI.Holder.getApi().getColonyManager().getControllerForWorld(player.getEntityWorld()).getColony(colonyId);
        if (chkColony == null)
        {
            return false;
        }
        return canCommandSenderUseCommand(theCommand)
                 && canRankUseCommand(chkColony, player);
    }

    /**
     * Will check to see if play is Opped for the given command name.
     *
     * @param sender  to check the player using the command.
     * @param cmdName the name of the command to be checked.
     * @return boolean
     */
    @NotNull
    public boolean isPlayerOpped(@NotNull final ICommandSender sender, String cmdName)
    {
        if (sender instanceof EntityPlayer)
        {
            return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
                     .canSendCommands(((EntityPlayer) sender).getGameProfile());
        }
        return true;
    }

    /**
     * Will check the config file to see if players are allowed to use the command that is sent here.
     *
     * @param theCommand which command to check if the player can use it
     * @return boolean
     */
    public boolean canCommandSenderUseCommand(Commands theCommand)
    {
        switch (theCommand)
        {
            case HOMETP:
                return Configurations.canPlayerUseHomeTPCommand;
            case COLONYTP:
                return Configurations.canPlayerUseColonyTPCommand;
            case RTP:
                return Configurations.canPlayerUseRTPCommand;
            case KILLCITIZENS:
                return Configurations.canPlayerUseKillCitizensCommand;
            case CITIZENINFO:
                return Configurations.canPlayerUseCitizenInfoCommand;
            case LISTCITIZENS:
                return Configurations.canPlayerUseListCitizensCommand;
            case RESPAWNCITIZENS:
                return Configurations.canPlayerRespawnCitizensCommand;
            case SHOWCOLONYINFO:
                return Configurations.canPlayerUseShowColonyInfoCommand;
            case ADDOFFICER:
                return Configurations.canPlayerUseAddOfficerCommand;
            case DELETECOLONY:
                return Configurations.canPlayerUseDeleteColonyCommand;
            case REFRESH_COLONY:
                return Configurations.canPlayerUseRefreshColonyCommand;
            case MC_BACKUP:
                return Configurations.canPlayerUseBackupCommand;
            default:
                return false;
        }
    }

    /**
     * Checks if the player has the permission to use the command.
     * By default officer and owner, overwrite this if other required.
     *
     * @param colony the colony.
     * @param player the player.
     * @return true if so.
     */
    public boolean canRankUseCommand(@NotNull final IColony colony, @NotNull final EntityPlayer player)
    {
        return colony.getPermissions().getRank(player.getUniqueID()).equals(Rank.OFFICER) || colony.getPermissions().getRank(player.getUniqueID()).equals(Rank.OWNER);
    }

    enum Commands
    {
        CITIZENINFO,
        COLONYTP,
        RTP,
        DELETECOLONY,
        KILLCITIZENS,
        LISTCITIZENS,
        RESPAWNCITIZENS,
        SHOWCOLONYINFO,
        ADDOFFICER,
        CHANGE_COLONY_OWNER,
        REFRESH_COLONY,
        HOMETP,
        MC_BACKUP
    }
}
