package edu.vanier.strawberries.Models;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;

import java.net.URL;
import java.util.Objects;

public class Switch extends Component {
    private boolean isClosed;
    private double mouseOffsetX;
    private double mouseOffsetY;
    private Image openImg, closedImg, rOpenImg, rClosedImg;

    public Switch(Node begin, Node end, boolean isClosed, boolean diagramView) {
        super(begin, end, diagramView);
        this.isClosed = isClosed;

        //load images
        try {
            URL openURL = getClass().getResource("/images/open_switch_diagram.png");
            URL closedURL = getClass().getResource("/images/closed_switch_diagram.png");
            URL rOpenURL = getClass().getResource("/images/switch_open_real.png");
            URL rClosedURL = getClass().getResource("/images/switch_closed_real.png");

            openImg = new Image(Objects.requireNonNull(openURL).toExternalForm());
            closedImg = new Image(Objects.requireNonNull(closedURL).toExternalForm());
            rOpenImg = new Image(Objects.requireNonNull(rOpenURL).toExternalForm());
            rClosedImg = new Image(Objects.requireNonNull(rClosedURL).toExternalForm());

        } catch (Exception e) {
            System.out.println("Could not fetch resource");
        }

        DIAGRAM_DISPLAY = openImg;
        IMAGE_DISPLAY = rOpenImg;
        display = diagramView ? DIAGRAM_DISPLAY : IMAGE_DISPLAY;
    }

    /**
     * Toggle the switch's current state (from open to closed, and vice-versa)
     */
    public void toggle() {
        isClosed = !isClosed;
        // updates image to show closed/open state
        if(isClosed) {
            DIAGRAM_DISPLAY = closedImg;
            IMAGE_DISPLAY = rClosedImg;
        }
        else {
            DIAGRAM_DISPLAY = openImg;
            IMAGE_DISPLAY = rOpenImg;
        }

        display = diagramView ? DIAGRAM_DISPLAY : IMAGE_DISPLAY;
    }

    /**
     * Verify if the switch is open or closed
     * @return true if the switch is closed
     */
    public boolean isClosed() {
        return isClosed;
    }

    /**
     * Creates a new Component of the same type and properties as the current (calling) component
     *
     * @return a new instance of Component
     */
    @Override
    public Component createCopy() {
        return new Switch(Node.copyOf(begin),Node.copyOf(end),isClosed,diagramView);
    }
}
