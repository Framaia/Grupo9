package dungeon.core.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dungeon.core.Player;

public class PlayerCombatComponent {

    private final Player player;
    private float attackCooldown = 0.5f;
    private float lastAttackTime = 0f;

    private boolean isAttacking = false;
    private float attackTimer = 0f;
    private final float attackDisplayTime = 0.2f;  // Tempo para mostrar sprite com espada

    public PlayerCombatComponent(Player player) {
        this.player = player;
    }

    public void update(float deltaTime) {
        lastAttackTime += deltaTime;

        if (attackTimer > 0) {
            attackTimer -= deltaTime;
            isAttacking = true;
        } else {
            isAttacking = false;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && lastAttackTime >= attackCooldown) {
            attack();
            lastAttackTime = 0;
            attackTimer = attackDisplayTime;
        }
    }

    private void attack() {
        // Aqui podes colocar a lógica de ataque real ao inimigo
        System.out.println("Ataque realizado!");
    }

    public boolean isAttacking() {
        return isAttacking;
    }
}
