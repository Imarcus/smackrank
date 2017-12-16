$(function() {
    $.urlParam = function(name){
        var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
        return results[1] || 0;
    }

    $.get('/get-player-statistics', {league : $.urlParam('league'), player : $.urlParam('player')},
        function(playerStats, success) {
            createDonut(playerStats);
            appendStatistics(playerStats);
        })

    $('#back-button').on('click', function(e) {
        var leagueName = $.urlParam('league');
        var url = window.location.href;
        url = url.split("/")[0];
        url += '/league.html?league=' + leagueName;
        window.location.href = url;
    })

    function appendStatistics(stats) {
        var leftCol = $('#left-col');
        $('#title-col').append($('<h1 class="display-1">').text(stats.name));
        $('#title-col').append($('<hr style="margin-bottom:5px !important; margin-top:5px !important; " />'));
        leftCol.append($('<h1 class="display-4">').text('Statistics'));
        leftCol.append($('<p>').text('Matches played: ' + stats.matchesPlayed));
        leftCol.append($('<p>').text('Matches won: ' + stats.matchesWon));
        leftCol.append($('<p>').text('Matches lost: ' + stats.matchesLost));
        leftCol.append($('<p>').text('Matches draw: ' + stats.matchesDraw));
        leftCol.append($('<p>').text('Total points scored: ' + stats.totalScoreWon));
        leftCol.append($('<p>').text('Total points conceded: ' + stats.totalScoreConceded));
        leftCol.append($('<p>').text('Favourite: ' + stats.playerMostWon));
        leftCol.append($('<p>').text('Nemesis: ' + stats.playerMostLost));
        leftCol.append($('<p>').text('Average points scored per match: ' + stats.averageScoreWonPerMatch));
        leftCol.append($('<p>').text('Average points conceded per match: ' + stats.averageScoreConcededPerMatch));
        leftCol.append($('<p>').text('Highest rating ever: ' + stats.highestRatingEver));
        leftCol.append($('<p>').text('Lowest rating ever: ' + stats.lowestRatingEver));



    }

    function createDonut(playerStats) {
        var data = [playerStats.matchesWon, playerStats.matchesLost, playerStats.matchesDraw];
        var winPercent = Math.round((playerStats.matchesWon / playerStats.matchesPlayed)*100);
        var lossPercent = Math.round((playerStats.matchesLost / playerStats.matchesPlayed)*100);
        var drawPercent = Math.round((playerStats.matchesDraw / playerStats.matchesPlayed)*100);
        var dataset = [];
        if(winPercent > 1) {
            dataset.push({name: 'Wins', percent: winPercent});
        }
        if(lossPercent > 1) {
            dataset.push({name: 'Loss', percent: lossPercent});
        }
        if(drawPercent > 1) {
            dataset.push({name: 'Draw', percent: drawPercent});
        }

        var width = 250,
            height = 250,
            radius = Math.min(width, height) / 2;
        var color = ["#45d63e", "#d63e3e", "#1560d8"];

        var arc = d3.arc()
            .outerRadius(radius - 10)
            .innerRadius(radius - radius*1/2);

        var pie = d3.pie()
            .sort(null)
            .value(function(d) { return d.percent; })
            .padAngle(0.02);

        var svg = d3.select("#right-col").append("svg")
            .attr("width", width + 100)
            .attr("height", height)
        .append("g")
            .attr("transform", "translate(" + width / 2 + "," + height / 2 + ")");

        var g = svg.selectAll(".arc")
          .data(pie(dataset))
        .enter().append("g")
          .attr("class", "arc");

        g.append("path")
            .attr("d", arc)
            .style("fill", function(d, i) { return color[i]; })
            .transition()
                .duration(1000)
                .attrTween("d", function (d) {
                    var i = d3.interpolate(d.startAngle, d.endAngle);
                    return function (t) {
                        d.endAngle = i(t);
                        return arc(d);
                    }
                });


        var legendRectSize = 18;
        var legendSpacing = 4;

        var legData = ['Wins','Losses','Draws'];
        var legend = svg.selectAll('.legend')
            .data(legData)
            .enter()
            .append('g')
            .attr('class', 'legend')
            .attr('transform', function(d, i) {
                var height = legendRectSize + legendSpacing;
                var offset =  -50;
                var horz = -2 * legendRectSize;
                var vert = i * height - offset;
                return 'translate(' + radius + ',' + vert + ')';
            });

        svg.selectAll('text')
            .data(pie(dataset))
            .enter()
            .append('text')
            .attr('transform', function(d) {
                return 'translate(' + arc.centroid(d) + ')';
            })
            .attr("dy", ".4em")
            .attr("text-anchor", "middle")
            .text(function(d){
                 return d.data.percent+"%";
             })
             .style('fill','#fff')
             .style('font-size','12px');

        legend.append('rect')
            .attr('width', legendRectSize)
            .attr('height', legendRectSize)
            .style('fill',  function(d, i) { return color[i]; })
            .style('stroke',  function(d, i) { return color[i]; });

        legend.append('text')
            .attr('x', legendRectSize + legendSpacing)
            .attr('y', legendRectSize - legendSpacing)
            .text(function(d) { return d; });
    }
});