$(function() {
    $.urlParam = function(name){
        var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
        return results[1] || 0;
    }

    $.get('/get-player-statistics', {league : $.urlParam('league'), player : $.urlParam('player')},
    function(playerStats, succes) {
        var data = [playerStats.matchesWon, playerStats.matchesLost, playerStats.matchesDraw];
        var width = 250,
            height = 250,
            radius = Math.min(width, height) / 2;
        var color = d3.scaleOrdinal()
            .range(["#55dd55", "#dd5656", "#1560d8"]);

        var arc = d3.arc()
            .outerRadius(radius - 10)
            .innerRadius(radius - radius*1/2);

        var pie = d3.pie()
            .sort(null)
            .value(function(d) { return d; });

        var svg = d3.select("#left-col").append("svg")
            .attr("width", width + 100)
            .attr("height", height)
        .append("g")
            .attr("transform", "translate(" + width / 2 + "," + height / 2 + ")");

        var g = svg.selectAll(".arc")
          .data(pie(data))
        .enter().append("g")
          .attr("class", "arc");

        g.append("path")
            .attr("d", arc)
            .style("fill", function(d) { return color(d.data); });


        var legendRectSize = 18;
        var legendSpacing = 4;

        var legData = ['Wins','Draws','Losses'];
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

        legend.append('rect')
            .attr('width', legendRectSize)
            .attr('height', legendRectSize)
            .style('fill', color)
            .style('stroke', color);

        legend.append('text')
            .attr('x', legendRectSize + legendSpacing)
            .attr('y', legendRectSize - legendSpacing)
            .text(function(d) { return d; });
    })
});