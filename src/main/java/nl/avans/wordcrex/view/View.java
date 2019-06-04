package nl.avans.wordcrex.view;

import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.widget.Widget;

import java.util.List;

public abstract class View<T extends Controller> extends Widget {
    protected final T controller;

    public View(T controller) {
        this.controller = controller;
    }

    public boolean shouldReinitialize() {
        return false;
    }

    public void focus(){
        for(int i = 0; i < this.getChildren().size(); i++){
            if (this.getChildren().get(i).getWantFocus() && !(i + 1 >= this.getChildren().size()))
            {
                this.getChildren().get(i + 1).setActive(true);
                this.getChildren().get(i).setWantFocus(false);
            } else if(this.getChildren().get(i).getWantFocus() && i + 1 >= this.getChildren().size()) {
                this.getChildren().get(0).setActive(true);
                this.getChildren().get(i).setWantFocus(false);
            }
        }
    }
}
