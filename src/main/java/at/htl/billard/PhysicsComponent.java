package at.htl.billard;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.core.math.Vec2;

import java.util.ArrayList;
import java.util.List;

public class PhysicsComponent extends Component {

    private Vec2 velocity = new Vec2(0, 0);
    private static final double FRICTION = 0.99;  // Reibungswert (Kugel wird pro Frame minimal abgebremst)
    private static final double MIN_VELOCITY = 0.1;  // Mindestgeschwindigkeit, ab der die Kugel komplett stoppt

    // Spielfeld-Grenzen
    private double minX = 0;
    private double minY = 0;
    private double maxX = 800;
    private double maxY = 600;
    private static final double BALL_RADIUS = 13;  // Radius der Kugel

    private List<Vec2> pockets = new ArrayList<>();
    private double pocketRadius = 0;

    public PhysicsComponent() {
    }

    public void setPockets(List<Vec2> pockets, double pocketRadius) {
        this.pockets = pockets;
        this.pocketRadius = pocketRadius;
    }

    @Override
    public void onUpdate(double tpf) {
        // Geschwindigkeit auf die aktuelle Position anwenden
        // Mit Faktor 100 skaliert, um die Bewegung sichtbar zu machen
        entity.translate(velocity.mul(tpf * 100));

        // Randkollisionen prüfen und Kugel abprallen lassen
        checkBoundaries();

        // Reibung anwenden (Kugel wird mit der Zeit langsamer)
        velocity = velocity.mul(FRICTION);

        // Kugel stoppen, wenn sie sehr langsam ist
        if (velocity.length() < MIN_VELOCITY) {
            velocity = new Vec2(0, 0);
        }
    }

    private void checkBoundaries() {
        // Aktuelle Position der Kugel holen
        double x = entity.getX();
        double y = entity.getY();
        double centerX = x + BALL_RADIUS;
        double centerY = y + BALL_RADIUS;

        // Wenn die Kugel im Bereich eines Lochs ist, keine Bandenkollision prüfen
        if (pockets != null && !pockets.isEmpty()) {
            for (Vec2 pocket : pockets) {
                if (new Vec2(centerX, centerY).distance(pocket) < pocketRadius * 1.5) {
                    return; // Kugel ist beim Loch, keine Bandenkollision mehr
                }
            }
        }

        // Linken Rand überprüfen
        if (x < minX) {
            entity.setX(minX);
            velocity = new Vec2(-velocity.x * 0.8, velocity.y);  // Abprallen mit 80% Energieverlust
        }

        // Rechten Rand überprüfen (Kugeldurchmesser abziehen für genaue Kollision)
        if (x + BALL_RADIUS * 2 > maxX) {
            entity.setX(maxX - BALL_RADIUS * 2);
            velocity = new Vec2(-velocity.x * 0.8, velocity.y);  // Abprallen mit 80% Energieverlust
        }

        // Oberen Rand überprüfen
        if (y < minY) {
            entity.setY(minY);
            velocity = new Vec2(velocity.x, -velocity.y * 0.8);  // Abprallen mit 80% Energieverlust
        }

        // Unteren Rand überprüfen (Kugeldurchmesser abziehen für genaue Kollision)
        if (y + BALL_RADIUS * 2 > maxY) {
            entity.setY(maxY - BALL_RADIUS * 2);
            velocity = new Vec2(velocity.x, -velocity.y * 0.8);  // Abprallen mit 80% Energieverlust
        }
    }

    public void setBounds(double minX, double minY, double maxX, double maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public void setVelocity(Vec2 vel) {
        this.velocity = vel;
    }

    public Vec2 getVelocity() {
        return velocity;
    }

    public void addVelocity(Vec2 vel) {
        this.velocity = this.velocity.add(vel);
    }
}
