import java.util.Random;

public class SpaceMath {
	public static Random rand = new Random();
	/**
	 * Spherical Calculations
	 * 
	 */
	public static double sphereVolume (double r){
		return FOURπOVER3 * r * r * r;
	}
	public static double sphereRadius (double volume){
		return Math.cbrt(volume / FOURπOVER3);
	}
	/*
	 * Not actually a sine wave. But it looks like one. Does not accept values <
	 * 3Pi
	 */
	public static float fastSine(float θ) {
		float sin = 0;
		if (θ > 6.28318531f)
			θ %= 6.28318531f;
		// always wrap input angle to -PI..PI
		if (θ < -3.14159265f)
			θ += 6.28318531f;
		else if (θ > 3.14159265f)
			θ -= 6.28318531f;

		// compute sine
		if (θ < 0)
			sin = 1.27323954f * θ + .405284735f * θ * θ;
		else
			sin = 1.27323954f * θ - 0.405284735f * θ * θ;
		return sin;

	}

	public static float fastCosine(float θ) {
		return fastSine(θ += 1.57079632f);

	}

	public static final float coeff_1 = (float) (Math.PI * .25f);
	public static final float coeff_2 = 3 * coeff_1;

	// an s curve that looks like an atan. 5x the frequency of Math.atan2.
	public static float fastatan2(float x, float y) {
		float abs_y = Math.abs(y);
		float angle;
		if (x >= 0) {
			float r = (x - abs_y) / (x + abs_y);
			angle = coeff_1 - coeff_1 * r;
		} else {
			float r = (x + abs_y) / (abs_y - x);
			angle = coeff_2 - coeff_1 * r;
		}
		return y < 0d ? -angle : angle;
	}

	// slower but more accurate approximations.
	public static float goodSine(float θ) {
		float sin = fastSine(θ);
		if (sin < 0)
			sin = .225f * (sin * -sin - sin) + sin;
		else
			sin = .225f * (sin * sin - sin) + sin;
		return sin;
	}

	public static float goodCosine(float θ) {
		return goodSine(θ += 1.57079632f);
	}

	// other mathematical methods
	public static float hypot(float a, float b) {
		return (float) Math.sqrt(a * a + b * b);
	}

	// http://atoms.alife.co.uk/sqrt/SquareRoot.java

	public final static int[] table = { 0, 16, 22, 27, 32, 35, 39, 42, 45, 48,
			50, 53, 55, 57, 59, 61, 64, 65, 67, 69, 71, 73, 75, 76, 78, 80, 81,
			83, 84, 86, 87, 89, 90, 91, 93, 94, 96, 97, 98, 99, 101, 102, 103,
			104, 106, 107, 108, 109, 110, 112, 113, 114, 115, 116, 117, 118,
			119, 120, 121, 122, 123, 124, 125, 126, 128, 128, 129, 130, 131,
			132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144,
			144, 145, 146, 147, 148, 149, 150, 150, 151, 152, 153, 154, 155,
			155, 156, 157, 158, 159, 160, 160, 161, 162, 163, 163, 164, 165,
			166, 167, 167, 168, 169, 170, 170, 171, 172, 173, 173, 174, 175,
			176, 176, 177, 178, 178, 179, 180, 181, 181, 182, 183, 183, 184,
			185, 185, 186, 187, 187, 188, 189, 189, 190, 191, 192, 192, 193,
			193, 194, 195, 195, 196, 197, 197, 198, 199, 199, 200, 201, 201,
			202, 203, 203, 204, 204, 205, 206, 206, 207, 208, 208, 209, 209,
			210, 211, 211, 212, 212, 213, 214, 214, 215, 215, 216, 217, 217,
			218, 218, 219, 219, 220, 221, 221, 222, 222, 223, 224, 224, 225,
			225, 226, 226, 227, 227, 228, 229, 229, 230, 230, 231, 231, 232,
			232, 233, 234, 234, 235, 235, 236, 236, 237, 237, 238, 238, 239,
			240, 240, 241, 241, 242, 242, 243, 243, 244, 244, 245, 245, 246,
			246, 247, 247, 248, 248, 249, 249, 250, 250, 251, 251, 252, 252,
			253, 253, 254, 254, 255 };

	/**
	 * A faster replacement for (int)(java.lang.Math.sqrt(x)). Completely
	 * accurate for x < 2147483648 (i.e. 2^31)...
	 */
	public static int intSqrt(int x) {
		int xn;

		if (x >= 0x10000) {
			if (x >= 0x1000000) {
				if (x >= 0x10000000) {
					if (x >= 0x40000000) {
						xn = table[x >> 24] << 8;
					} else {
						xn = table[x >> 22] << 7;
					}
				} else {
					if (x >= 0x4000000) {
						xn = table[x >> 20] << 6;
					} else {
						xn = table[x >> 18] << 5;
					}
				}

				xn = (xn + 1 + (x / xn)) >> 1;
				xn = (xn + 1 + (x / xn)) >> 1;
				return ((xn * xn) > x) ? --xn : xn;
			} else {
				if (x >= 0x100000) {
					if (x >= 0x400000) {
						xn = table[x >> 16] << 4;
					} else {
						xn = table[x >> 14] << 3;
					}
				} else {
					if (x >= 0x40000) {
						xn = table[x >> 12] << 2;
					} else {
						xn = table[x >> 10] << 1;
					}
				}

				xn = (xn + 1 + (x / xn)) >> 1;

				return ((xn * xn) > x) ? --xn : xn;
			}
		} else {
			if (x >= 0x100) {
				if (x >= 0x1000) {
					if (x >= 0x4000) {
						xn = (table[x >> 8]) + 1;
					} else {
						xn = (table[x >> 6] >> 1) + 1;
					}
				} else {
					if (x >= 0x400) {
						xn = (table[x >> 4] >> 2) + 1;
					} else {
						xn = (table[x >> 2] >> 3) + 1;
					}
				}

				return ((xn * xn) > x) ? --xn : xn;
			} else {
				if (x >= 0) {
					return table[x] >> 4;
				}
			}
		}

		illegalArgument();
		return -1;
	}

	/**
	 * A faster replacement for (int)(java.lang.Math.sqrt(x)). Completely
	 * accurate for x < 2147483648 (i.e. 2^31)... Adjusted to more closely
	 * approximate "(int)(java.lang.Math.sqrt(x) + 0.5)" by Jeff Lawson.
	 */
	public static int accurateIntSqrt(int x) {
		int xn;

		if (x >= 0x10000) {
			if (x >= 0x1000000) {
				if (x >= 0x10000000) {
					if (x >= 0x40000000) {
						xn = table[x >> 24] << 8;
					} else {
						xn = table[x >> 22] << 7;
					}
				} else {
					if (x >= 0x4000000) {
						xn = table[x >> 20] << 6;
					} else {
						xn = table[x >> 18] << 5;
					}
				}

				xn = (xn + 1 + (x / xn)) >> 1;
				xn = (xn + 1 + (x / xn)) >> 1;
				return adjustment(x, xn);
			} else {
				if (x >= 0x100000) {
					if (x >= 0x400000) {
						xn = table[x >> 16] << 4;
					} else {
						xn = table[x >> 14] << 3;
					}
				} else {
					if (x >= 0x40000) {
						xn = table[x >> 12] << 2;
					} else {
						xn = table[x >> 10] << 1;
					}
				}

				xn = (xn + 1 + (x / xn)) >> 1;

				return adjustment(x, xn);
			}
		} else {
			if (x >= 0x100) {
				if (x >= 0x1000) {
					if (x >= 0x4000) {
						xn = (table[x >> 8]) + 1;
					} else {
						xn = (table[x >> 6] >> 1) + 1;
					}
				} else {
					if (x >= 0x400) {
						xn = (table[x >> 4] >> 2) + 1;
					} else {
						xn = (table[x >> 2] >> 3) + 1;
					}
				}

				return adjustment(x, xn);
			} else {
				if (x >= 0) {
					return adjustment(x, table[x] >> 4);
				}
			}
		}

		illegalArgument();
		return -1;
	}

	private static int adjustment(int x, int xn) {
		// Added by Jeff Lawson:
		// need to test:
		// if |xn * xn - x| > |x - (xn-1) * (xn-1)| then xn-1 is more accurate
		// if |xn * xn - x| > |(xn+1) * (xn+1) - x| then xn+1 is more accurate
		// or, for all cases except x == 0:
		// if |xn * xn - x| > x - xn * xn + 2 * xn - 1 then xn-1 is more
		// accurate
		// if |xn * xn - x| > xn * xn + 2 * xn + 1 - x then xn+1 is more
		// accurate
		int xn2 = xn * xn;

		// |xn * xn - x|
		int comparitor0 = xn2 - x;
		if (comparitor0 < 0) {
			comparitor0 = -comparitor0;
		}

		int twice_xn = xn << 1;

		// |x - (xn-1) * (xn-1)|
		int comparitor1 = x - xn2 + twice_xn - 1;
		if (comparitor1 < 0) { // need to correct for x == 0 case?
			comparitor1 = -comparitor1; // only gets here when x == 0
		}

		// |(xn+1) * (xn+1) - x|
		int comparitor2 = xn2 + twice_xn + 1 - x;

		if (comparitor0 > comparitor1) {
			return (comparitor1 > comparitor2) ? ++xn : --xn;
		}

		return (comparitor0 > comparitor2) ? ++xn : xn;
	}

	/**
	 * A *much* faster replacement for (int)(java.lang.Math.sqrt(x)). Completely
	 * accurate for x < 289...
	 */
	public static int fastIntSqrt(int x) {
		if (x >= 0x10000) {
			if (x >= 0x1000000) {
				if (x >= 0x10000000) {
					if (x >= 0x40000000) {
						return (table[x >> 24] << 8);
					} else {
						return (table[x >> 22] << 7);
					}
				} else if (x >= 0x4000000) {
					return (table[x >> 20] << 6);
				} else {
					return (table[x >> 18] << 5);
				}
			} else if (x >= 0x100000) {
				if (x >= 0x400000) {
					return (table[x >> 16] << 4);
				} else {
					return (table[x >> 14] << 3);
				}
			} else if (x >= 0x40000) {
				return (table[x >> 12] << 2);
			} else {
				return (table[x >> 10] << 1);
			}
		} else if (x >= 0x100) {
			if (x >= 0x1000) {
				if (x >= 0x4000) {
					return (table[x >> 8]);
				} else {
					return (table[x >> 6] >> 1);
				}
			} else if (x >= 0x400) {
				return (table[x >> 4] >> 2);
			} else {
				return (table[x >> 2] >> 3);
			}
		} else if (x >= 0) {
			return table[x] >> 4;
		}
		illegalArgument();
		return -1;
	}

	private static void illegalArgument() {
		throw new IllegalArgumentException(
				"Attemt to take the square root of negative number");
	}
	//more math
	public static int factorial(int n){
		int n1 = 1;
		for(int i = 2; i < n; i++){
			n1 *= i;
		}
		return n1;
	}
	public static Vector polarToCartesian(double θ, double r){
		return new Vector(Math.cos(θ) * r, Math.sin(θ) * r);
	}
	//useful common calculations
	public static int randomColor(){
		return rand.nextInt(0x1000000);
	}
	public static final double TWOπ = Math.PI * 2;
	public static double randomθ(){
		return rand.nextDouble() * TWOπ;
	}
	public static double getθ(Vector p1, Vector p2){
		return Math.atan2(p2.y - p1.y, p2.x - p1.x);
	}
	//Physics stuff
	public static final double FOURπOVER3 = 4 * Math.PI / 3;
	public static double getDensity(double mass, double radius){
		return FOURπOVER3 * radius * radius * radius / mass;
	}
	public static double getRadius(double mass, double density){
		return Math.cbrt(mass / (density * FOURπOVER3));

		//m/(4/3pi r cube) = density
	}

	//gravity is calculated using the classical universal gravitational equations.  Sorry no general relativity support yet.  
	public static Vector getGravitationalForce(double G, double m1, double m2, Vector p1, Vector p2){
		float θ = (float)Math.atan2(p1.y - p2.y, p1.x - p2.x);
		double magnitude = getGravitationalForce(G, m1, m2, p1.distance(p2));
		return new Vector(SpaceMath.fastCosine(θ) * magnitude, SpaceMath.fastSine(θ) * magnitude);
	}
	public static double getGravitationalForce(double G, double m1, double m2, double displacement){
		return G * m1 * m2 / (displacement * displacement);
	}
	
	/**
	*	Returns the necessary velocity vector for m1 to orbit around m2, rotation having a positive θ values.
	*/
	public static final double πOVER2 = Math.PI * .5;
	public static Vector getOrbitalVector(double G, double m2, Vector p1, Vector p2){
		double θ = SpaceMath.getθ(p1, p2) + πOVER2;
		double magnitude = getOrbitalVelocity(G, m2, p1, p2);
		return polarToCartesian(θ, magnitude);
	}
	/**
	*	Returns the necessary velocity for m1 to orbit around m2.  
	*/
	public static double getOrbitalVelocity(double G, double m2, Vector p1, Vector p2){
		//a = v*v/r
		//v = sqrt(ar)
		double r = p1.distance(p2);
		//double a = G * m2 / (r * r);
		double ar = G * m2 / r;
		return Math.sqrt(ar);
	}
}
