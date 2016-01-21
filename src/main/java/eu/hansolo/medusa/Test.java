/*
 * Copyright (c) 2016 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.medusa;

import eu.hansolo.medusa.Gauge.ButtonEvent;
import eu.hansolo.medusa.Gauge.KnobType;
import eu.hansolo.medusa.Gauge.LcdFont;
import eu.hansolo.medusa.Gauge.LedType;
import eu.hansolo.medusa.Gauge.NeedleShape;
import eu.hansolo.medusa.Gauge.NeedleSize;
import eu.hansolo.medusa.Gauge.ScaleDirection;
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.Gauge.ThresholdEvent;
import eu.hansolo.medusa.Gauge.TickLabelLocation;
import eu.hansolo.medusa.Gauge.TickLabelOrientation;
import eu.hansolo.medusa.Gauge.TickMarkType;
import eu.hansolo.medusa.GaugeDesign.GaugeBackground;
import eu.hansolo.medusa.Marker.MarkerType;
import eu.hansolo.medusa.skins.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;

import java.awt.*;
import java.util.Random;

import static javafx.scene.paint.Color.RED;


/**
 * User: hansolo
 * Date: 04.01.16
 * Time: 06:31
 */
public class Test extends Application {
    private static final Random         RND = new Random();
    private static       int            noOfNodes = 0;
    private              Gauge          gauge;
    private              long           lastTimerCall;
    private              AnimationTimer timer;

    @Override public void init() {
        gauge = GaugeBuilder.create()
                            .skin(TinySkin.class)
                            .animated(true)
                            .sections(new Section(0, 20, Color.rgb(0, 0, 200, 0.5)),
                                      new Section(20, 40, Color.rgb(0, 200, 200, 0.5)),
                                      new Section(40, 60, Color.rgb(0, 200, 0, 0.5)),
                                      new Section(60, 80, Color.rgb(200, 200, 0, 0.5)),
                                      new Section(80, 100, Color.rgb(200, 0, 0, 0.5)))
                            .build();

        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(long now) {
                if (now > lastTimerCall + 3_000_000_000l) {
                    gauge.setValue(RND.nextDouble() * gauge.getRange() + gauge.getMinValue());
                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(gauge);
        pane.setPadding(new Insets(10));
        LinearGradient gradient = new LinearGradient(0, 0, 0, pane.getLayoutBounds().getHeight(),
                                                     false, CycleMethod.NO_CYCLE,
                                                     new Stop(0.0, Color.rgb(38, 38, 38)),
                                                     new Stop(1.0, Color.rgb(15, 15, 15)));
        //pane.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));
        //pane.setBackground(new Background(new BackgroundFill(Color.rgb(39,44,50), CornerRadii.EMPTY, Insets.EMPTY)));
        //pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(pane);

        stage.setTitle("Medusa");
        stage.setScene(scene);
        stage.show();

        //gauge.setValue(0.35);

        // Calculate number of nodes
        calcNoOfNodes(gauge);
        System.out.println(noOfNodes + " Nodes in SceneGraph");

        timer.start();
    }

    @Override public void stop() {
        System.exit(0);
    }


    // ******************** Misc **********************************************
    private static void calcNoOfNodes(Node node) {
        if (node instanceof Parent) {
            if (((Parent) node).getChildrenUnmodifiable().size() != 0) {
                ObservableList<Node> tempChildren = ((Parent) node).getChildrenUnmodifiable();
                noOfNodes += tempChildren.size();
                tempChildren.forEach(n -> calcNoOfNodes(n));
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
