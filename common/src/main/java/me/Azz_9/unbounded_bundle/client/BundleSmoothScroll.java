package me.Azz_9.unbounded_bundle.client;

public class BundleSmoothScroll {

	private static float smoothOffset = 0f;
	private static int targetOffset = 0;
	private static long lastFrameTime = System.nanoTime();

	// Vitesse d'interpolation — plus c'est haut, plus c'est rapide
	private static final float LERP_SPEED = 20f;

	public static void update(int newTarget) {
		long now = System.nanoTime();
		float deltaTime = (now - lastFrameTime) / 1_000_000_000f;
		lastFrameTime = now;

		// Clamp deltaTime pour éviter un saut si le jeu lag
		deltaTime = Math.min(deltaTime, 0.1f);

		targetOffset = newTarget;

		// Lerp exponentiel : indépendant du framerate
		smoothOffset += (targetOffset - smoothOffset) * (1f - (float) Math.exp(-LERP_SPEED * deltaTime));
	}

	public static void reset(int target) {
		smoothOffset = target;
		targetOffset = target;
		lastFrameTime = System.nanoTime();
	}

	public static float getSmoothOffset() {
		return smoothOffset;
	}
}