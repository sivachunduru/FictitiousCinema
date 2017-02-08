var express = require('express');
var app = express();
var bodyParser = require('body-parser');
var logfmt = require("logfmt");
var mysql = require('mysql');

app.use(logfmt.requestLogger());

// configure app to use bodyParser()
// this will let us get the data from a POST
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json({ type: '*/*' }));

var router = express.Router();
router.use(function (request, response, next) {
  console.log("REQUEST:" + request.method + "   " + request.url);
  console.log("BODY:" + JSON.stringify(request.body));
  response.setHeader('Access-Control-Allow-Origin', '*');
  response.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS, PUT, PATCH, DELETE');
  response.setHeader('Access-Control-Allow-Headers', 'X-Requested-With,content-type');
  response.setHeader('Access-Control-Allow-Credentials', true);
  next();
});

var PORT = Number(process.env.PORT || 8125);

var pool = mysql.createPool({
	connecitonLimit: 25,
    host: process.env.MOVIES_DATABASE_HOST || '192.168.99.100',
    database: 'cinema',
    user: 'cinema_service',
    password: 'welcome1',
    port:3306
});


/**
 * GET / 
 * Returns a list of movies 
 */
router.route('/movies/').get(function (request, response) {
    console.log("GET ALL MOVIES");
    pool.getConnection(function (error, con){
        if (!!error) {
            console.log ('Error in getting connection');
            response.json({"code" : 500, "status" : "Error in getting connection"});
            return;
        }
        console.log ('Got the connection');
        con.query('SELECT cinema_id, title, director, casting FROM cinemas', function(error, rows, fields) {
            con.release();
            console.log ('Successfully queried '+rows.length +' rows.');
            console.log(rows);
            response.json(rows);
        });
        con.on('error', function(err) {
            con.release();
            console.log ('Error in firing query');
            response.json({"code" : 500, "status" : "Error in connection database"});
            return;     
        });
    });
});

/**
 * GET /searchType/searchValue 
 * Returns a list of employees that match the criteria 
 * searchType could be either 'title' or 'director'
 */
router.route('/movies/:searchType/:searchValue').get(function (request, response) {
    console.log("GET MOVIES BY CRITERIA");
    pool.getConnection(function (error, con){
        if (!!error) {
            console.log ('Error in getting connection');
            response.json({"code" : 500, "status" : "Error in getting connection"});
            return;
        }
        console.log ('Got the connection');
        var searchType = request.params.searchType;
        var searchValue = request.params.searchValue;
        con.query('SELECT cinema_id, title, director, casting FROM cinemas WHERE '+searchType+' = ?', [searchValue], function(error, rows, fields) {
            con.release();
            console.log ('Successfully queried.\n');
            console.log(rows);
            response.json(rows);
        });
        con.on('error', function(err) {      
            con.release();
            console.log ('Error in firing query');
            response.json({"code" : 500, "status" : "Error in connection database"});
            return;     
        });
    });
});

/**
 * POST / 
 * Saves a new movie 
 */
router.route('/movies/').post(function (request, response) {
    console.log("POST NEW MOVIE:");
    pool.getConnection(function (error, con){
        if (!!error) {
            console.log ('Error in getting connection');
            response.json({"code" : 500, "status" : "Error in getting connection"});
            return;
        }
        console.log ('Got the connection');
        var body = request.body;
        con.query("INSERT into cinemas (cinema_id, title, director, casting) VALUES (?, ?, ?, ?)", 
        [body.cinema_id, body.title, body.director, body.casting], function (err, result) {
            con.release();
            console.log ('Successfully inserted '+result.affectedRows +' rows.');
            console.log(result);
            response.json({"code" : 200, "status" : 'Successfully inserted '+result.affectedRows +' rows.'});
        });
        con.on('error', function(err) {      
            con.release();
            console.log (err.message);
            response.json({"code" : 500, "status" : "Error saving new movie to DB"});
            return;     
        });
    });
});

/**
 * PUT / 
 * Update a movie 
 */

router.route('/movies/:cinema_id').put(function (request, response) {
    console.log("PUT MOVIE:");
    pool.getConnection(function (error, con){
        if (!!error) {
            console.log ('Error in getting connection');
            response.json({"code" : 500, "status" : "Error in getting connection"});
            return;
        }
        var body = request.body;
        var cinema_id = request.params.cinema_id;
        con.query("UPDATE cinemas set title=?, director=?, casting=? where cinema_id=?", 
        [body.title, body.director, body.casting, cinema_id], function (err, result) {
            con.release();
            console.log ("Successfully updated "+result.changedRows +" rows.");
            console.log(result);
            response.json({"code" : 200, "status" : "Successfully updated "+result.changedRows +" rows."});
        });
        con.on('error', function(err) {      
            con.release();
            console.log (err.message);
            response.json({"code" : 500, "status" : "Error updating movie."});
            return;     
        });
    });
});

/**
 * DELETE / 
 * Delete a movie 
 */

router.route('/movies/:cinema_id').delete(function (request, response) {
    console.log("DELETE MOVIE ID:"+request.params.cinema_id);
    pool.getConnection(function (error, con){
        if (!!error) {
            console.log ('Error in getting connection');
            response.json({"code" : 500, "status" : "Error in getting connection"});
            return;
        }
        var cinema_id = request.params.cinema_id;
        con.query("DELETE from cinemas where cinema_id=?", 
        [cinema_id], function (err, result) {
            con.release();
            console.log ("Successfully deleted "+result.changedRows +" rows.");
            console.log(result);
            response.json({"code" : 200, "status" : "Successfully deleted "+result.changedRows +" rows."});
        });
        con.on('error', function(err) {      
            con.release();
            console.log (err.message);
            response.json({"code" : 500, "status" : "Error deleting movie."});
            return;     
        });
    });
});

app.use(express.static('static'));
app.use('/', router);
app.listen(PORT, function() {
  console.log("Listening on " + PORT);
});