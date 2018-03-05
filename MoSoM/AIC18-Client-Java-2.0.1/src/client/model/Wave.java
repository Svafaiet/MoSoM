package client.model;

public class Wave {

    private int length;
    private int unitsPerCell;

    public Wave(int step, int moveSpeed) {
        //TODO
        length = (int) Math.pow(step, 1);
        unitsPerCell = (int) Math.pow(step, 1);
        this.moveSpeed = moveSpeed;
    }

    public int getUnitsPerCell() {
        return unitsPerCell;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setUnitsPerCell(int unitsPerCell) {
        this.unitsPerCell = unitsPerCell;
    }

    private int moveSpeed;

    public int getMoveSpeed() {
        return moveSpeed;
    }

}
