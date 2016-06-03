package org.canova.image.loader;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.nd4j.linalg.api.buffer.BaseDataBuffer;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.complex.IComplexDouble;
import org.nd4j.linalg.api.complex.IComplexFloat;

public class ImageByteBuffer extends BaseDataBuffer {

    public ImageByteBuffer(byte[] data, int length) {
        super(Unpooled.wrappedBuffer(data), length);
        this.elementSize = 1;
    }

    // TODO add back override when nd4j updated with method signatures
    
    @Override
    protected DataBuffer create(long length) {
        return null;
    }

    public ImageByteBuffer(int i) {
        super(i);
    }

    public ImageByteBuffer(double[] doubles) {
        super(doubles);
    }

    public ImageByteBuffer(float[] floats) {
        super(floats);
    }

    public ImageByteBuffer(int[] ints) {
        super(ints);
    }

    public ImageByteBuffer(ByteBuf byteBuf, int i) {
        super(byteBuf, i);
    }

    public DataBuffer create(int i) { return new ImageByteBuffer(i);}

    @Override
    public DataBuffer create(double[] doubles) {
        return new ImageByteBuffer(doubles);
    }

    @Override
    public DataBuffer create(float[] floats) {
        return new ImageByteBuffer(floats);
    }

    @Override
    public DataBuffer create(int[] ints) {
        return new ImageByteBuffer(ints);
    }

    @Override
    public DataBuffer create(ByteBuf byteBuf, int i) {
        return new ImageByteBuffer(byteBuf, i);
    }

    @Override
    public IComplexFloat getComplexFloat(long i) {
        return null;
    }

    @Override
    public IComplexDouble getComplexDouble(long i) {
        return null;
    }

    public IComplexFloat getComplexFloat(int i) {
        return null;
    }

    public IComplexDouble getComplexDouble(int i) {
        return null;
    }

    @Override
    protected void initTypeAndSize() {

    }

    @Override
    public int getElementSize() {
        return this.elementSize;
    }

    @Override
    public Type dataType() {
        return DataBuffer.Type.INT;
    }

    @Override
    public int getInt(long i) {
        return ((int) wrappedBuffer.get((int) i)) & 0xff;
    }

    public int getInt(int i) {
        return ((int) wrappedBuffer.get((int) i)) & 0xff;
    }

    @Override
    public float getFloat(long i) {
        return (float)getInt(i);
    }

    @Override
    public Number getNumber(long i) {
        return getInt(i);
    }

    @Override
    public double getDouble(long i) {
        return (double)getInt(i);
    }

    public float getFloat(int i) {
        return (float)getInt(i);
    }

    public Number getNumber(int i) {
        return getInt(i);
    }

    public double getDouble(int i) {
        return (double)getInt(i);
    }

}
