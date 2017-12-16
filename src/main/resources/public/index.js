$(function(){

    $.urlParam = function(name){
        var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
        return results[1] || 0;
   	}

    var leagueList = $("#league-list");
    $.get("/get-all-league-names", function(leagueNames, status) {
        for(var i = 0; i < leagueNames.length; i++) {
            var listItem = '<a class="list-group-item list-group-item-action" href="/league.html?league=' + leagueNames[i] + '">' + leagueNames[i] + '</a>';
            leagueList.append(listItem);
        }
    });

    $('#create-league-form').on('submit', function(e){
        e.preventDefault();
        var leagueName = $("#leagueNameInput").val();
        var playerNames = $("#playersTextArea").val();
        $.get("/create-league-with-players", {leagueName : leagueName, playerNames: playerNames}, function(){
            window.location.href = window.location.href + "league.html?league=" + leagueName ;
        });
    });
});