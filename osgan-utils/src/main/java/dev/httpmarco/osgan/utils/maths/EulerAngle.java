package dev.httpmarco.osgan.utils.maths;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EulerAngle {

    public static final EulerAngle ZERO = new EulerAngle(0, 0, 0);
    private double x, y, z;

    public EulerAngle add(double x, double y, double z) {
        return new EulerAngle(this.x + x, this.y + y, this.z + z);
    }

    public EulerAngle subtract(double x, double y, double z) {
        return add(-x, -y, -z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (EulerAngle) o;
        return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0 && Double.compare(that.z, z) == 0;
    }

    @Override
    public int hashCode() {
        var temp = Double.doubleToLongBits(x);
        var result = Long.hashCode(temp);
        temp = Double.doubleToLongBits(y);
        result = 31 * result + Long.hashCode(temp);
        temp = Double.doubleToLongBits(z);
        result = 31 * result + Long.hashCode(temp);
        return result;
    }
}
