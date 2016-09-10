function [ att_gain ] = gain_split2( data )

    % Split the data into subsets in order to minimize entropy.
    
    att_gain = gain(data);

    % determine the best attribute
    best_att = 0;
    max_gain = -1;
    for i = 1 : size(att_gain, 1)
        if(att_gain(i) > max_gain)
            max_gain = att_gain(i);
            best_att = i;
        end
    end

    ind = ones(4);
    for i = 1 : size(data,1)
       branchs(data(i,best_att), :, ind(data(i,best_att))) = data(i,:); 
       ind(data(i,best_att)) = ind(data(i,best_att)) + 1;
    end
    
    % For each subset, calculate the gain for each attribute
    att_gain = zeros(4,6);
    for i = 1 : size(branchs,1)
        branch_data = [];
        ind = 1;
        for j = 1 : size(branchs,3)
            if(branchs(i,1,j) == 0)
                break;
            end
            branch_data(ind, :) = branchs(i,:,j);
            ind = ind + 1;
        end
        att_gain(i,:) = gain(branch_data);  
    end
   
end



