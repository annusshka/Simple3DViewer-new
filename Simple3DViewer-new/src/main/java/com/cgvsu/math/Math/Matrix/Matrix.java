package com.cgvsu.math.Math.Matrix;

import com.cgvsu.math.Math.Vector.Vector;

public abstract class Matrix {

    public static class MatrixException extends Exception {
        public MatrixException(String message) {
            super(message);
        }
    }

    protected final int size;

    protected final int length;

    protected float[] vector;

    public Matrix(float[] vector, final int size) {
        if (vector.length == size * size) {
            this.vector = vector;
            this.size = size;
            this.length = size * size;
        } else if (size > 0) {
            float[] rightVector = new float[size * size];
            System.arraycopy(vector, 0, rightVector, 0, Math.min(vector.length, size * size));
            this.vector = rightVector;
            this.size = size;
            this.length = size * size;
        } else {
            this.vector = new float[0];
            this.size = 0;
            this.length = 0;
        }
    }

    static final float EPS = 1e-5f;

    public int getSize() {
        return size;
    }

    public int getLength() {
        return length;
    }

    public float[] getVector() {
        return vector;
    }

    public float get(final int index) {
        float element = 0;
        try {
            element = vector[index];
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException(e);
        }
        return element;
    }

    public void set(final int index, final float value) {
        if (index >= 0 && index < getVector().length) {
            vector[index] = value;
        }
    }

    public void setData(float[] data) {
        if (data.length == length) {
            this.vector = data;
        } else {
            float[] rightVector = new float[size];
            System.arraycopy(data, 0, rightVector, 0, Math.min(data.length, size));
            this.vector = rightVector;
        }
    }

    public boolean isEqualSize(final Matrix matrix2) {
        return this.getLength() == matrix2.getLength();
    }

    public boolean isEqualMatrix(final Matrix matrix2) {
        if (isEqualSize(matrix2)) {
            for (int index = 0; index < this.getSize(); index++) {
                if (Math.abs(this.get(index) - matrix2.get(index)) >= EPS) {
                    return false;
                }
            }
        } else {
            return false;
        }

        return true;
    }

    /**
     * Метод проверяет является ли матрица единичной (на главной диагонали находятся единицы, умноженные на
     * какую-то константу, а все остальные значения - нули)
     * @param matrix проверяемая матрица
     * @param eps показывает точность вычислений
     * @return результат проверки
     */
    public static boolean isIdentityMatrix(final Matrix matrix, final float eps) {
        float firstElement = matrix.get(0);
        if (Math.abs(firstElement) < eps) {
            return false;
        }

        int indexMainDiagonal = 0;
        for (int index = 0; index < matrix.getLength(); index++) {
            if (index == indexMainDiagonal * matrix.getSize() + indexMainDiagonal) {
                if (Math.abs(matrix.get(index) - firstElement) >= eps) {
                    return false;
                }

                indexMainDiagonal++;
            } else if (Math.abs(matrix.get(index)) >= eps){
                return false;
            }
        }

        return true;
    }

    public abstract Matrix createIdentityMatrix(final float value);

    public abstract Matrix createIdentityMatrix();

    public abstract Matrix getZeroMatrix(final int size);
    public abstract Vector getZeroVector(final int size);

    /**
     * Метод транспонирует матрицу. Значения на главное диагонали не меняются, остальные элементы меняются
     * местами с элементами, у которых индекс строки совпадает с их индексом столбца, а индекс столбца -
     * с индексом строки
     * @return транспонированную матрицу
     */
    public static Matrix transposeMatrix(Matrix matrix) {
        int indexCol = 0, indexRow = 0;
        int size = matrix.getSize();

        for (int index = 0; index < size - 1; index++) {
            indexCol = index + 1;
            indexRow = index;

            while (indexCol < size) {
                matrix.swapElements(indexRow * size + indexCol, indexCol * size + indexRow);
                indexCol++;
            }
        }
        return matrix;
    }


    /**
     * Метод складывает две матрицы
     * @param matrix1 матрица 1
     * @param matrix2 матрица 2
     * @return полученную матрицу
     * @throws MatrixException оповещает о том, что пытались перемножить разные по размеру матрицы
     */
    public static Matrix sumMatrix(final Matrix matrix1, final Matrix matrix2) throws MatrixException {
        Matrix matrix = matrix1.getZeroMatrix(matrix1.size);

        if (matrix1.isEqualSize(matrix2)) {
            for (int index = 0; index < matrix.getLength(); index++) {
                matrix.getVector()[index] = matrix1.get(index) + matrix2.get(index);
            }

        } else {
            throw new MatrixException("Matrices of different sizes can't be summed");
        }

        return matrix;
    }

    public static Matrix minusMatrix(final Matrix matrix1, final Matrix matrix2) throws MatrixException {
        Matrix matrix = matrix1.getZeroMatrix(matrix1.size);

        if (matrix1.isEqualSize(matrix2)) {
            for (int index = 0; index < matrix.getLength(); index++) {
                matrix.getVector()[index] = matrix1.get(index)- matrix2.get(index);
            }

        } else {
            throw new MatrixException("Matrices of different sizes can't be summed");
        }

        return matrix;
    }

    public void multiplicateOnValue(final float value) {
        for (int index = 0; index < this.getLength(); index++) {
            this.set(index, this.get(index) * value);
        }
    }

    public void divideOnValue(final float value) throws MatrixException {
        if (Math.abs(value) < EPS) {
            throw new MatrixException("Division by zero");
        }
        multiplicateOnValue(1.0f / value);
    }

    public Vector multiplicateOnVector(final Vector vector) throws MatrixException {
        if (this.getSize() != vector.getSize()) {
            throw new MatrixException("Different sizes can't be multiplicated");
        }

        Vector result = vector.getZeroVector(size);
        int indexShift = 0;

        for (int indexRow = 0; indexRow < size; indexRow++) {
            indexShift = 0;

            while (indexShift < size) {
                result.getVector()[indexRow] += this.get(indexRow * size + indexShift) *
                        vector.get(indexShift);
                indexShift++;
            }
        }

        return result;
    }

    public static Matrix multiplicateMatrices(final Matrix matrix1, final Matrix matrix2)
            throws MatrixException {
        if (!matrix1.isEqualSize(matrix2)) {
            throw new MatrixException("Different sizes can't be multiplicated");
        }

        Matrix matrix = matrix1.getZeroMatrix(matrix1.getSize());
        final int size = matrix.getSize();
        int indexShift = 0, indexCol = 0, indexRow = 0;

        for (int index = 0; index < matrix.getLength(); index++) {
            indexShift = 0;
            indexCol = index % size;
            indexRow = index / size;

            while (indexShift < size) {
                matrix.getVector()[index] +=
                        matrix1.get(indexRow * size + indexShift) * matrix2.get(indexShift * size + indexCol);
                indexShift++;
            }
        }

        return matrix;
    }

    /**
     * Метод реализует поиск детерминанта путём превращения матрицы в треугольную, а затем перемножение элементов
     * на главной диагонали
     * @return определитель
     */
    public float getDeterminant() {
        final float detCoeff = this.getTriangleMatrix();
        float determinant = 1;

        for (int index = 0; index < size; index++) {
            determinant *= this.get(index * size + index);
        }

        if (detCoeff != 0) {
            determinant /= detCoeff;
        } else {
            determinant = 0;
        }

        return determinant;
    }

    /**
     * Метод превращения исходной матрицы в треугольную, используется для поиска определителя
     * @return коэффициент определителя, полученный вследствие преобразований матрицы
     */
    private float getTriangleMatrix() {
        int indexCol = 0, changingIndexRow = 0;
        float detCoeff = 1;

        for (int index = 0; index < size - 1; index++) {
            indexCol = 0;
            changingIndexRow = this.getSwapIndexRow(index);

            if (changingIndexRow == -2) {
                detCoeff = 0;
                continue;
            } else if (changingIndexRow != -1) {
                while (indexCol < size) {
                    this.swapElements(index * size + indexCol, changingIndexRow * size + indexCol);
                    indexCol++;
                }
                detCoeff *= -1;
            }

            detCoeff *= this.getZeroCol(index);
        }
        return detCoeff;
    }

    /**
     * Метод преобразует исходную матрицу путём обнуления столбца по элементу с переданным индексом
     * @param index индекс строки/столбца, по нему находят индекс элемента, по которому происходит обнуление
     * @return коэффициент определителя, полученный вследствие матричных преобразований
     */
    private float getZeroCol(final int index) {
        int indexNextRow = index + 1;
        float coeff, coeffNextRow, coeffActualRow = this.get(index * size + index);
        float detCoeff = 1;

        while (indexNextRow < size) {
            coeff = getCoeff(this.get(indexNextRow * size + index), this.get(index * size + index));
            coeffNextRow = this.get(indexNextRow * size + index);

            if (coeff % 1 != 0) {
                this.multiplicateOnCoeff(coeffNextRow, coeffActualRow, index, indexNextRow, 0);
                detCoeff *= coeffActualRow;
            } else {
                this.multiplicateOnCoeff(coeff, 1, index, indexNextRow, 0);
            }

            indexNextRow++;
        }
        return detCoeff;
    }

    /**
     * Метод преобразует строку
     * @param coeffNextRow коэффициент элемента строки, которую будут преобразовывать
     * @param coeffActualRow коэффициент элемента строки, по которому будет преобразована следующая строка
     * @param index индекс строки элемента, по которому будут преобразовывать следующую строку
     * @param indexRow индекс строки элемента, которую будут преобразовывать
     * @param indexCol индекс столбца, с которого начинается преобразование
     */
    private void multiplicateOnCoeff(
            final float coeffNextRow, final float coeffActualRow,
            final int index, final int indexRow, int indexCol) {

        while (indexCol < size) {
            int actualIndex = indexRow * size + indexCol;
            int prevIndex = index * size + indexCol;

            this.getVector()[actualIndex] = this.get(actualIndex) * coeffActualRow -
                    this.get(prevIndex) * coeffNextRow;
            indexCol++;
        }
    }

    private int getSwapIndexRow(final int index) {
        int changingIndexRow = -1;
        int actualIndex = index * size + index;
        float minValue = Math.abs(this.get(actualIndex));

        for (int indexRow = index + 1; indexRow < size; indexRow++) {
            actualIndex = indexRow * size + index;
            if (this.get(actualIndex) != 0 && (minValue == 0 || Math.abs(this.get(actualIndex)) < minValue)) {
                minValue = this.get(actualIndex);
                changingIndexRow = indexRow;
            }
        }
        if (minValue == 0) {
            return -2;
        }

        return changingIndexRow;
    }

    /**
     * Метод получения обратной матрицы. Изначальную матрицу умножает на единичную, затем превращает изначальную
     * матрицу в треугольную с параллельным преобразованием единичной, затем вызывает обратный ход метода
     * @param matrix исходная матрица
     * @return обратный ход метода, который возвращает обратную матрицу
     *  @throws MatrixException сообщает о том, что матрица не имеет обратной матрицы
     */
    public static Matrix getInverseMatrix(final Matrix matrix) throws MatrixException {
        Matrix unitMatrix = matrix.createIdentityMatrix();
        float coeff, coeffNextRow, coeffActualRow;
        int indexCol, indexRow, changingIndexRow;
        int size = matrix.getSize();

        for (int index = 0; index < size - 1; index++) {
            indexCol = index;
            indexRow = index + 1;

            changingIndexRow = matrix.getSwapIndexRow(index);
            // changingIndexRow == -2 -> на главной диагонали находится ноль, значит определитель равен 0
            if (changingIndexRow == -2) {
                throw new MatrixException("Matrix hasn't inverse matrix");
            } else if (changingIndexRow != -1) {
                while (indexCol < size) {
                    matrix.swapElements(index * size + indexCol, changingIndexRow * size + indexCol);
                    indexCol++;
                }
            }

            coeffActualRow = matrix.get(index * size + index);
            while (indexRow < size) {
                coeff = getCoeff(matrix.get(indexRow * size + index), matrix.get(index * size + index));
                coeffNextRow = matrix.get(indexRow * size + index);

                if (coeff % 1 != 0) {
                    matrix.multiplicateOnCoeff(coeffNextRow, coeffActualRow, index, indexRow, 0);
                    unitMatrix.multiplicateOnCoeff(coeffNextRow, coeffActualRow, index, indexRow, 0);
                } else {
                    matrix.multiplicateOnCoeff(coeff, 1, index, indexRow, 0);
                    unitMatrix.multiplicateOnCoeff(coeff, 1, index, indexRow, 0);
                }
                indexRow++;
            }
        }

        return reversePassOfInverseMatrixMethod(matrix, unitMatrix);
    }

    /**
     * Метод обратного хода matrix преобразует в единичную матрицу, а unitMatrix - в искомую обратную матрицу.
     * @param matrix треугольная матрица, полученная преобразованиями в методе getInverseMatrix
     * @param unitMatrix преобразованная в методе getInverseMatrix единичная матрица
     * @return обратную матрицу
     */
    private static Matrix reversePassOfInverseMatrixMethod(final Matrix matrix, final Matrix unitMatrix) {
        int indexRow, indexCol;
        int size = matrix.getSize();
        float coeff;

        for (int index = matrix.getSize() - 1; index >= 0; index--) {
            indexRow = index - 1;
            indexCol = 0;

            coeff = getCoeff(1, matrix.get(index * size + index));
            while (coeff != 1 && indexCol < size) {
                matrix.getVector()[index * size + indexCol] *= coeff;
                unitMatrix.getVector()[index * size + indexCol] *= coeff;
                indexCol++;
            }

            while (indexRow >= 0) {
                indexCol = 0;
                coeff = getCoeff(matrix.get(indexRow * size + index), matrix.get(index * size + index));
                matrix.getVector()[indexRow * size + index] -= coeff;

                while (indexCol < matrix.getSize()) {
                    unitMatrix.getVector()[indexRow * size + indexCol] -= unitMatrix.get(index * size + indexCol) * coeff;
                    indexCol++;
                }

                indexRow--;
            }
        }

        return unitMatrix;
    }

    private void swapElements(final int index, final int changingIndex) {
        final float changingValue = this.get(index);
        this.getVector()[index] = this.get(changingIndex);
        this.getVector()[changingIndex] = changingValue;
    }

    /**
     * Метод решает систему линейных уравнений методом Гаусса. Если система будет иметь множество решений,
     * метод найдёт частное решение, подставив значение 1.
     * Метод реализует прямой ход методом Гаусса и вызывает обратный ход.
     * @param matrix основная матрица СЛАУ
     * @param vector вектор-столбец свободных членов
     * @return вектор-столбец неизвестных переменных
     * @throws MatrixException оповещает, если система не имеет решений
     */
    public static Vector solutionByGaussMethod(final Matrix matrix, final Vector vector) throws MatrixException {
        float coeff, coeffNextRow, coeffActualRow;
        int indexCol, indexRow, changingIndexRow;
        int size = matrix.getSize();

        for (int index = 0; index < size - 1; index++) {
            indexCol = index;
            indexRow = index + 1;
            changingIndexRow = matrix.getSwapIndexRow(index);

            // -2 -> имеет бесконечно много корней - переходим на следующую итерацию цикла
            // -1 -> меняем строки местами
            if (changingIndexRow == -2) {
                continue;
            } else if (changingIndexRow != -1) {
                while (indexCol < size) {
                    matrix.swapElements(index * size + indexCol, changingIndexRow * size + indexCol);
                    indexCol++;
                }
                vector.swapElements(index, changingIndexRow);
            }

            while (indexRow < size) {
                coeff = getCoeff(matrix.get(indexRow * size + index), matrix.get(index * size + index));
                coeffNextRow = matrix.get(indexRow * size + index);
                coeffActualRow = matrix.get(index * size + index);

                if (coeff % 1 == 0) {
                    matrix.multiplicateOnCoeff(coeff, 1, index, indexRow, index);
                    vector.getVector()[indexRow] = vector.get(indexRow) - vector.get(index) * coeff;
                } else {
                    matrix.multiplicateOnCoeff(coeffNextRow, coeffActualRow, index, indexRow, index);
                    vector.getVector()[indexRow] = vector.get(indexRow) * coeffActualRow -
                            vector.get(index) * coeffNextRow;

                }

                indexRow++;
            }
        }
        return reversePassOfGaussMethod(matrix, vector);
    }

    /**
     * метод реализует обратный ход метода Гаусса
     * @param matrix треугольная матрица СЛАУ
     * @param vector вектор-столбец свободных членов
     * @return вектор-столбец неизвестных переменных
     * @throws MatrixException оповещает, если система не имеет решений
     */
    private static Vector reversePassOfGaussMethod(final Matrix matrix, final Vector vector) throws MatrixException {
        Vector solutionVector = vector.getZeroVector(vector.getSize());
        int indexCol;
        int size = matrix.getSize();
        float sum, matrixValue, vectorValue;

        for (int index = size - 1; index >= 0; index--) {
            sum = 0;
            indexCol = size - 1;

            while (indexCol > index) {
                matrixValue = matrix.get(index * size + indexCol);
                vectorValue = solutionVector.get(indexCol);
                sum += matrixValue * vectorValue;
                indexCol--;
            }

            // Не существует решений
            // Иначе множество решений, поэтому ищем частное
            if (matrix.get(index * size + index) == 0 &&
                    ((sum == 0 && vector.get(index) != 0) || (sum != 0 && vector.get(index) == 0))) {
                throw new MatrixException("There are no solutions");
            } else if (matrix.get(index * size + index) == 0) {
                solutionVector.set(index, 1);
                continue;
            }

            solutionVector.getVector()[index] = (vector.get(index) - sum) / matrix.get(index * size + index);
        }

        return solutionVector;
    }

    private static float getCoeff(final float value1, final float value2) {
        if (Math.abs(value2) < EPS) {
            throw new ArithmeticException("Division by zero");
        }

        return value1 / value2;
    }
}
