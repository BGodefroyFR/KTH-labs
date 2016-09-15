import java.util.*;

public class HMM {

    public static Matrix getObsPrediction(Model model, int time) {

        Matrix res = new Matrix(model.Pi);

        for(int i = 1; i < time; i++)
           res = new Matrix(res.times(model.A));

        res = new Matrix(res.times(model.B));

        return res;
    }

    public static double getObsSeqProb(Model model, Matrix M) {

        Matrix Alpha = new Matrix(M.getColumnNum(), model.A.getColumnNum());

        for(int i = 1; i <= model.A.getColumnNum(); i++)
        {
            Alpha.set(1, i, model.Pi.get(1, i) * model.B.get(i, (int)M.get(1, 1) + 1));
        }

        for(int t = 2; t <= M.getColumnNum(); t++)
        {
            for(int i = 1; i <= model.A.getColumnNum(); i++)
            {
                double tmp = 0;
                for(int j = 1; j <= model.A.getColumnNum(); j++)
                {
                    tmp += model.A.get(j, i) * Alpha.get(t-1, j);
                }

                Alpha.set(t, i, tmp * model.B.get(i, (int)M.get(1, t) + 1));
            }
        }

        double res = 0;
        for(int j = 1; j <= model.A.getColumnNum(); j++)
        {
            res += Alpha.get(M.getColumnNum(), j);
        }

        return res;
    }

    public static State getMostLikelyLastHiddenState(Model model, Matrix M) {

        Matrix Theta = new Matrix(M.getColumnNum(), model.A.getColumnNum());
        Matrix Theta_idx = new Matrix(M.getColumnNum(), model.A.getColumnNum());

        for(int i = 1; i <= model.A.getRowNum(); i++)
        {
            Theta.set(1, i, model.B.get(i, (int)M.get(1, 1) + 1) * model.Pi.get(1, i));
            Theta_idx.set(1, i, i-1);
        }

        for(int t = 2; t <= M.getColumnNum(); t++)
        {
            for(int i = 1; i <= model.A.getColumnNum(); i++)
            {
                double max = 0;
                int bestIdx = -1;

                for(int j = 1; j <= model.A.getColumnNum(); j++)
                {
                    double tmp2 = model.A.get(j, i) * Theta.get(t-1, j) * model.B.get(i, (int)M.get(1, t) + 1);

                    if(tmp2 >= max)
                    {
                        max = tmp2;
                        bestIdx = j;
                    }
                }
                Theta.set(t, i, max);
                Theta_idx.set(t, i, bestIdx-1);
            }
        }

        Matrix finalState = new Matrix(1, model.A.getColumnNum());
        for(int i = 1; i <= Theta.getColumnNum(); i++)
        {
            finalState.set(1, i, Theta.get(Theta.getRowNum(), i));
        }

        Matrix finalObs = finalState.times(model.A).times(model.B);

        double bestProb = 0.0;
        int bestProbObs = 0;

        for(int i = 1; i <= finalObs.getColumnNum(); i++)
        {
            if(finalObs.get(1, i) >= bestProb)
            {
                bestProb = finalObs.get(1, i);
                bestProbObs = i - 1;
            }
        }

        State state = new State(bestProbObs, bestProb);
        return state;
    }
    
    public static List<Integer> getMostLikelyHiddenStatesSequence(Model model, Matrix M) {

        Matrix Theta = new Matrix(M.getColumnNum(), model.A.getColumnNum());
        Matrix Theta_idx = new Matrix(M.getColumnNum(), model.A.getColumnNum());

        for(int i = 1; i <= model.A.getRowNum(); i++)
        {
            Theta.set(1, i, model.B.get(i, (int)M.get(1, 1) + 1) * model.Pi.get(1, i));
            Theta_idx.set(1, i, i-1);
        }

        for(int t = 2; t <= M.getColumnNum(); t++)
        {
            for(int i = 1; i <= model.A.getColumnNum(); i++)
            {
                double max = 0;
                int bestIdx = -1;

                for(int j = 1; j <= model.A.getColumnNum(); j++)
                {
                    double tmp2 = model.A.get(j, i) * Theta.get(t-1, j) * model.B.get(i, (int)M.get(1, t) + 1);

                    if(tmp2 >= max)
                    {
                        max = tmp2;
                        bestIdx = j;
                    }
                }
                Theta.set(t, i, max);
                Theta_idx.set(t, i, bestIdx-1);
            }
        }

        double maxProb = 0;
        int bestIdx = -1;
        for(int i = 1; i <= Theta.getColumnNum(); i++)
        {
            if(Theta.get(Theta.getRowNum(), i) >= maxProb)
            {
                maxProb = Theta.get(Theta.getRowNum(), i);
                bestIdx = i;
            }
        }

        List<Integer> res = new ArrayList<Integer>();
        int idx = bestIdx-1;

        res.add(0,idx);

        for(int i = Theta_idx.getRowNum(); i >= 2; i--)
        {
            idx = (int)Theta_idx.get(i, idx+1);
            res.add(0,idx);
        }

        return res;
    }

    public static Model estimateModel(Model intialModel, int[] O, int step)
    {
        double[][] A = Util.createArray2DFromMatrix(intialModel.A);
        double[][] B = Util.createArray2DFromMatrix(intialModel.B);
        double[] Pi = Util.createArray1DFromMatrix(intialModel.Pi);

        int maxIters = 10000;
        int iters = 0;
        double logProb = -1e300;
        double oldLogProb;

        double[][] newA = A;
        double[][] newB = B;

        int N = A.length;
        int M = B[0].length;
        int T = step;

        do {
            A = Util.getArrayCopy(newA);
            B = Util.getArrayCopy(newB);

            oldLogProb = logProb;

            double[] c = new double[T];
            double[][] Alpha = new double[T][N];
            double[][] Beta = new double[T][N];
            double[][][] DiGamma = new double[T][N][N];
            double[][] Gamma = new double[T][N];

            // compute Alpha_0(i)
            c[0] = 0;
            for(int i = 0; i <= N - 1; i++)
            {
                Alpha[0][i] = Pi[i] * B[i][(int)O[0]];
                c[0] = c[0] + Alpha[0][i];
            }

            // scale the Alpha_0(i)
            c[0] = 1.0 / c[0];
            for(int i = 0; i <= N - 1; i++)
                Alpha[0][i] = c[0] * Alpha[0][i];

            // compute Alpha_t(i)
            for(int t = 1; t <= T - 1; t++)
            {
                c[t] = 0;
                for(int i = 0; i <= N - 1; i++)
                {
                    Alpha[t][i] = 0;
                    for(int j = 0; j <= N - 1; j++)
                    {
                        Alpha[t][i] = Alpha[t][i] + Alpha[t-1][j] * A[j][i];
                    }
                    Alpha[t][i] = Alpha[t][i] * B[i][(int)O[t]];
                    c[t] = c[t] + Alpha[t][i];
                }

                // scale Alpha_t(i)
                c[t] = 1.0 / c[t];
                for(int i = 0; i <= N-1; i++)
                    Alpha[t][i] = c[t] * Alpha[t][i];
            }

            for(int i = 0; i <= N-1; i++)
                Beta[T-1][i] = c[T-1];

            // The Beta-pass
            for(int t = T-2; t >= 0; t--)
            {
                for(int i = 0; i <= N-1; i++)
                {
                    Beta[t][i] = 0;
                    for(int j = 0; j <= N-1; j++)
                        Beta[t][i] = Beta[t][i] + A[i][j] * B[j][(int)O[t+1]] * Beta[t+1][j];
                
                    // scale
                    Beta[t][i] = c[t] * Beta[t][i];
                }
            }

            // Compute Gamma and T-gamma
            for(int t = 0; t <= T-2; t++)
            {
                double denom = 0.0;
                for(int i = 0; i <= N-1; i++)
                {
                    for(int j = 0; j <= N-1; j++)
                    {
                        denom = denom + Alpha[t][i] * A[i][j] * B[j][(int)O[t+1]] * Beta[t+1][j];
                    }
                }
                for(int i = 0; i <= N-1; i++)
                {
                    Gamma[t][i] = 0;
                    for(int j = 0; j <= N-1; j++)
                    {
                        DiGamma[t][i][j] = (Alpha[t][i] * A[i][j] * B[j][(int)O[t+1]] * Beta[t+1][j]) / denom;
                        Gamma[t][i] = Gamma[t][i] + DiGamma[t][i][j];
                    }
                }
            }

            // special case
            double denom = 0;
            for(int i = 0; i <= N-1; i++)
                denom = denom + Alpha[T-1][i];
            for(int i = 0; i <= N-1; i++)
                Gamma[T-1][i] = Alpha[T-1][i] / denom;

            // Re-estimate A, B and Pi
            for(int i = 0; i <= N-1; i++)
                Pi[i] = Gamma[0][i];

            for(int i = 0; i <= N-1; i++)
            {
                for(int j = 0; j <= N-1; j++)
                {
                    double numer = 0.0;
                    denom = 0.0;
                    for(int t = 0; t <= T-2; t++)
                    {
                        numer = numer + DiGamma[t][i][j];
                        denom = denom + Gamma[t][i];
                    }
                    newA[i][j] = numer / denom;
                }
            }

            for(int i = 0; i <= N-1; i++)
            {
                for(int j = 0; j <= M-1; j++)
                {
                    double numer = 0.0;
                    denom = 0.0;

                    for(int t = 0; t <= T-1; t++)
                    {
                        if(O[t] == j)
                            numer = numer + Gamma[t][i];
                        denom = denom + Gamma[t][i];
                    }
                    newB[i][j] = numer / denom;
                }
            }

            // Compute log
            logProb = 0.0;
            for(int i = 0; i <= T-1; i++)
                logProb = logProb + Math.log(c[i]);
            logProb = (-1) * logProb;

            iters ++;

        } while(iters < maxIters && logProb > oldLogProb);

        Model estimateModel = new Model(A, B, Pi);
        return estimateModel;
    }
}