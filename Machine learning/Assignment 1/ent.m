function [ entropy ] = ent(data)

    nbTrue = 0;
    nbFalse = 0;

    for i = 1 : size(data,1)
       if(data(i, 7) == 0)
          nbTrue = nbTrue + 1;
       else
           nbFalse = nbFalse + 1;
       end
    end
    
    fprintf('Nb true: %d\n', nbTrue);
    fprintf('Nb false: %d\n', nbFalse);
    
    p0 = nbFalse / (nbTrue + nbFalse)
    p1 = nbTrue / (nbTrue + nbFalse)

    entropy = - p0 * log2(p0) - p1 * log2(p1);
end

