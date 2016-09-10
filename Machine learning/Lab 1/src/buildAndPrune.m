function [ fracs, errors ] = buildAndPrune( train_data, test_data )

    [n,m]=size(train_data);
    p=randperm(n);
    
    fracs = [0.3 0.4 0.5 0.6 0.7 0.8];
    errors = [];
    errors_prune = [];
    
    ind = 1;
    for i = 0.3 : 0.1 : 0.8
        frac=i;
        new_train_data=train_data(p(1:floor(n*frac)),:);
        prune_data=train_data(p(floor(n*frac)+1:n),:);
        
        T1=build_tree(new_train_data);
        T1p=prune_tree(T1,prune_data);
        
        errors(ind)=calculate_error(T1,test_data);
        errors_prune(ind)=calculate_error(T1p,test_data);
        
        ind = ind + 1;
    end

    figure 
    plot(fracs, errors);
end

