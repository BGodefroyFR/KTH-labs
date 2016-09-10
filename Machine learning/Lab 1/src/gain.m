function [ entGain ] = gain(data)

    attribute_prob = zeros(6, 4); % Store the probabilities

    for i = 1 : 6 % For all atributes

        for j = 1 : 4 % Test for all possible attribute values
            
			sub = subset(data,i,j);
			
            if(size(sub,1) > 0)
				nbTrue = 0;
				nbFalse = 0;
				for k = 1 : size(sub,1)
					if(sub(k,7) == 1)
					  nbTrue = nbTrue + 1;
					else
					  nbFalse = nbFalse + 1;
					end
				end
			   
				attribute_prob(i, j) = nbTrue / (nbTrue + nbFalse);
			else
				attribute_prob(i, j) = -1;
            end
        end
    end
	attribute_prob
	% Calculate gain
	entGain = zeros(6,1);
	former_entropy = ent(data)
	for i = 1 : 6		
		entGain(i,1) = former_entropy;
		for j = 1 : 4
            if(attribute_prob(i, j) > 0 && attribute_prob(i, j) < 1)
                attribute_entropy = - attribute_prob(i, j) * log2(attribute_prob(i, j)) - (1 - attribute_prob(i, j)) * log2(1 - attribute_prob(i, j));
                entGain(i,1) = entGain(i,1) - attribute_entropy * (size(subset(data,i,j), 1) / size(data, 1));
            end
		end
	end
end

