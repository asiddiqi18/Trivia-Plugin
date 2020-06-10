package me.marcarrots.trivia.menu;

import org.bukkit.entity.Player;

public class PlayerMenuUtility {

    private Player owner;


//    private Ticket ticket;

    private MenuType previousMenu = null;

    public PlayerMenuUtility(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

/*    public Ticket getTicket() {
       return ticket;
      }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
}
*/

    public MenuType getPreviousMenu() {
        return previousMenu;
    }

    public void setPreviousMenu(MenuType previousMenu) {
        this.previousMenu = previousMenu;
    }

}

