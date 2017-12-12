$(function() {
    $.urlParam = function(name){
        var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
        return results[1] || 0;
    }

    $.get("/get-league", { league : $.urlParam('league') }, function(data, status){
        $("#league-title").text(data.name);
        drawAllPlayerRatings(data);
        populatePlayMatchForm(data);
    });

    $.get("/get-league-statistics", {league : $.urlParam('league')}, function(data, status){
        console.log(data);
    })

    $('#add-player-form').on('submit', function(e){
        e.preventDefault();
        var leagueName = $.urlParam('league');
        var playerName = $("#addPlayerNameField").val();
        $.get("/add-player", { leagueName : leagueName, playerName : playerName }, function(data, status){
            location.reload();
        });
     });

    $("#play-match-form").on('submit', function(e){
        e.preventDefault();
        var leagueName = $.urlParam('league');
        var homePlayer = $('#homePlayerMatchSelect').val()[0];
        var awayPlayer = $('#awayPlayerMatchSelect').val()[0];
        var homeScore = $('#homePlayerScoreField').val();
        var awayScore = $('#awayPlayerScoreField').val();
        $.get("/play-match",
            { league : leagueName, homePlayer : homePlayer, awayPlayer : awayPlayer,
                homeScore : homeScore, awayScore : awayScore },
                function(data, status){
                    location.reload();
        });
    });

	function drawAllPlayerRatings(league) {
		$('#graph').empty();

		var margin = {top: 40, right: 80, bottom: 80, left: 70};
		var width = 960 - margin.left - margin.right,
    		height = 500 - margin.top - margin.bottom,
    		roundWidth = width/(league.currentRoundCount);

        // Define the div for the tooltip
        var tooltipDiv = d3.select("body").append("div")
            .attr("class", "tooltip")
            .style("opacity", 0);


		var svg = d3.select("#graph")
			.append("svg")
			.attr("width", width + margin.left + margin.right)
			.attr("height", height + margin.top + margin.bottom)
			.append("g")
    		.attr("transform", "translate(" + margin.left + "," + margin.top + ")");


		var xScale = d3.scaleLinear().domain([0, league.currentRoundCount]).range([0, width]);
        var xAxis = d3.axisBottom(xScale).tickFormat(d3.format(',d')).ticks(league.currentRoundCount).tickSize(-height);
        svg.append('g')
            .attr('transform', 'translate(0,' + (height) + ')')
            .classed('x-axis', true)
            .call(xAxis);

        var yScale = d3.scaleLinear().domain([1500 - league.maxRankDeviation, 1500 + league.maxRankDeviation])
            .range([height, 0]);
        svg.append('g').call(d3.axisLeft(yScale))
            .attr('transform', 'translate(0,0)');
        svg.append('g').call(d3.axisRight(yScale))
                    .attr('transform', 'translate(' + width + ',0)');

        for(j = 0; j < league.players.length; j++) {
            drawPlayerRating(svg, league, league.players[j], roundWidth, height, xScale, yScale, tooltipDiv);
         }
        drawLegend(league);
        drawMatchHistory(league);

        // text label for the x axis
        svg.append("text")
          .attr("transform",
                "translate(" + (width/2) + " ," +
                               (height + margin.top) + ")")
          .style("text-anchor", "middle")
          .text("Round");
        // text label for the y axis
        svg.append("text")
              .attr("transform", "rotate(-90)")
              .attr("y", 0 - margin.left)
              .attr("x",0 - (height / 2))
              .attr("dy", "1em")
              .style("text-anchor", "middle")
              .text("Rank");
	}

	function drawPlayerRating(svg, league, player, sectionLength, graphHeight, xScale, yScale, tooltipDiv) {
		var lineFunction = d3.line()
			.x(function(d) { return d.x; })
			.y(function(d) { return yScale(d.y); });

		var lineData = getLineData(league, player.name, sectionLength, graphHeight);
		var path = svg.append("path")
			.attr("d", lineFunction(lineData.array))
			.attr("stroke", player.colour)
			.attr("id", (player.name + '-path'))
			.on("mouseover", function(d) {
                setOpacity(lineData.id, "0.2");
			}).on("mouseout", function(d) {
                setOpacity(lineData.id, "1");
			});
        animatePath(path);



        svg.selectAll('.point').data(lineData.array).enter().append('circle')
            .attr('r', 4)
            .attr('transform', function(d) { return 'translate(' + d.x + ',' + yScale(d.y) + ')'; })
            .style("fill", player.colour)
            .on("mouseover", function(d) {
                 tooltipDiv.transition().duration(200)
                 .style("opacity", .9);
                 tooltipDiv.html("<b>" + player.name + "</b></br>Rating: " + d.y)
                 .style("left", (event.pageX - 40)+"px")
                 .style("top", (event.pageY - 56)+"px");
            })
            .on("mouseout", function(d) {
                tooltipDiv.transition()
                .duration(500)
                .style("opacity", 0);
            });
	}

    function setOpacity(loopId, opacity) {
        d3.selectAll('#graph > svg > g > path').each(
            function(d) {
                if(!(loopId === this.id)) {
                   this.setAttribute("opacity", opacity);
                }
            }
        );
    }

	function getLineData(league, playerName, sectionLength, graphHeight) {
		var lineData = {};
		lineData.id = playerName + "-path";
		var lineDataArray = [];
		for(i = 0; i < league.rounds.length; i++) {
		    var playerRank = league.rounds[i].rankMap[playerName];
		    if(playerRank) {
                var point = {};
		        point.x = i * sectionLength;
                point.y = playerRank;
                lineDataArray.push(point);
		    }
		}
		lineData.array = lineDataArray;
		return lineData;
	}

	function animatePath(path) {
	    var totalLength = path.node().getTotalLength();
        path.attr("stroke-dasharray", totalLength + " " + totalLength)
            .attr("stroke-dashoffset", totalLength)
            .transition()
              .duration(2000)
//              .ease(d3.easeLinear)
              .attr("stroke-dashoffset", 0);
	}

	function drawLegend(league) {

	    var legendList = $('#legend-list');
	    $.get('/get-league-statistics', { league : league.name },
            function(leagueStats, status) {
                for(h = 0; h < leagueStats.playerStatistics.length; h++) {

                    var playerStats = leagueStats.playerStatistics[h];
                    var playerTableRow =
                        $('<tr>')
                            .append($('<th scope="row">').text(h + 1))
                            .append($('<td id="' + playerStats.name + '-row" style="text-align: left">'))//.text(playerStats.name))
                            .append($('<td>').text(playerStats.matchesPlayed))
                            .append($('<td>').text(playerStats.matchesWon))
                            .append($('<td>').text(playerStats.matchesLost))
                            .append($('<td>').text((playerStats.matchesWon/playerStats.matchesLost).toFixed(2)))
                            .append($('<td>').text(playerStats.rating));
                    playerTableRow.attr("id", playerStats.name);
                    $('#playerTableBody').append(playerTableRow);

//                    var rectSize = 15;
                    var rectWidth = 15;
                    var rectHeight = 20;
                    var svg = d3.select('#' + playerStats.name + '-row').append('svg')
                        .attr('width',200).attr('height',rectHeight);
                    var rect = svg.append('rect').attr('fill',function(d) {return playerStats.colour;})
                        .attr('width',rectWidth).attr('height',rectHeight);
                    var text = svg.append('text')
                        .attr('x', rectHeight + 2)
                        .attr('y', 15)
                        .text(playerStats.name);


                    playerTableRow.on("mouseover", function(d) {
                        setOpacity((this.id + '-path'), "0.2");
                    }).on("mouseout", function(d) {
                       setOpacity((this.id + '-path'), "1");
                    });
                }

            })
//        $.get("/get-players-sorted", { league : league.name }, function(players, status) {
//            for(k = 0; k < players.length; k++) {
//                var player = players[k];
//
//
//
//                var playerListItem =
//                    $('<a class="list-group-item list-group-item-action flex-column align-items-start" style="background:' + player.colour + ';">')
//                        .append(
//                            $('<div class="d-flex w-100 justify-content-between">')
//                            .append(
//                                $('<h5 class="mb-1">')
//                                .text(
//                                    (k+1) + '. ' + player.name)
//                            ).append(
//                                $('<small>')
//                                .text('Rating')
//                                .append(
//                                    $('<p class="font-weight-bold">')
//                                    .text(player.rating)
//                                )
//                            )
//                        );
//                playerListItem.attr("id", player.name);
//                playerListItem.on("mouseover", function(d) {
//                    setOpacity((this.id + '-path'), "0.2");
//                }).on("mouseout", function(d) {
//                   setOpacity((this.id + '-path'), "1");
//               });
//                legendList.append(playerListItem);
//            }
//        });
    }

    function drawMatchHistory(league) {
        var matchList = $('#match-list');
        var matches = league.matches;

        for(y = 0; y < matches.length; y++) {
            var match = matches[y];
            var homePlayer, awayPlayer;
            if(match.homePlayerScore > match.awayPlayerScore) {
                homePlayer = '<strong>' + match.homePlayerName + ' - ' + match.homePlayerScore + "</strong>";
                awayPlayer = match.awayPlayerName + ' - ' + match.awayPlayerScore;
            } else if (match.awayPlayerScore > match.homePlayerScore) {
                awayPlayer = '<strong>' + match.awayPlayerName + ' - ' + match.awayPlayerScore + "</strong>";
                homePlayer = match.homePlayerName + ' - ' + match.homePlayerScore;
            } else {
                homePlayer = match.homePlayerName + ' - ' + match.homePlayerScore;
                awayPlayer = match.awayPlayerName + ' - ' + match.awayPlayerScore;
            }

            var matchListItem =
                $('<a class="list-group-item list-group-item-action" style="background-color:#f6f6f6;">')
                .append($('<div class="row">')
                    .append($('<div class="col">').append("hej"))
                    .append($('<div class="col">')
                        .append($('<div class="row">').append($('<div class="col align-self-end">').append(homePlayer)))
                        .append($('<div class="row">').append($('<div class="col align-self-end">').append(awayPlayer)))
                    )
                );
            matchList.append(matchListItem);
        }
    }

    function reloadWithParam(paramName, param) {
        var url = window.location.href;
        url = url.split("?")[0];
        url += '?' + paramName + '=' + param;
        window.location.href = url;
    }

    function populatePlayMatchForm(league) {
        var homePlayerSelect = $('#homePlayerMatchSelect');
        var awayPlayerSelect = $('#awayPlayerMatchSelect');
        for(u = 0; u < league.players.length; u++) {
            homePlayerSelect.append($('<option>').text(league.players[u].name));
            awayPlayerSelect.append($('<option>').text(league.players[u].name));
        }
    }

});
