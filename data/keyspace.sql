CREATE KEYSPACE busca
    WITH REPLICATION = {
        'class' : 'SimpleStrategy',
        'replication_factor' : 1 
    };
