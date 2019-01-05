/**
 * Created by User on 26/09/2018.
 */
var rsf = function rsf() {var min = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 0;var max = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 80;return Math.floor(Math.random() * 100) + min;};

var ctx1 = document.getElementById("chart-1").getContext('2d');
var myChart1 = new Chart(ctx1, {
    type: 'bar',
    data: {
        labels: ["Red", "Blue", "Yellow", "Green", "Purple", "Orange"],
        datasets: [{
            label: '# of Votes',
            data: [
                rsf(),
                rsf(),
                rsf(),
                rsf(),
                rsf(),
                rsf()],

            backgroundColor: [
                'rgba(255, 99, 132, 0.2)',
                'rgba(54, 162, 235, 0.2)',
                'rgba(255, 206, 86, 0.2)',
                'rgba(75, 192, 192, 0.2)',
                'rgba(153, 102, 255, 0.2)',
                'rgba(255, 159, 64, 0.2)'],

            borderColor: [
                'rgba(255,99,132,1)',
                'rgba(54, 162, 235, 1)',
                'rgba(255, 206, 86, 1)',
                'rgba(75, 192, 192, 1)',
                'rgba(153, 102, 255, 1)',
                'rgba(255, 159, 64, 1)'],

            borderWidth: 1 }] },


    options: {
        responsive: true,
        scales: {
            yAxes: [{
                ticks: {
                    beginAtZero: true } }] } } });






var ctx2 = document.getElementById("chart-2").getContext('2d');
var myChart2 = new Chart(ctx2, {
    type: 'line',
    data: {
        labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
        datasets: [{
            label: 'Foo',
            data: [
                rsf(),
                rsf(),
                rsf(),
                rsf(),
                rsf(),
                rsf()],

            backgroundColor: 'rgba(59, 175, 218, 0.3)',
            borderColor: 'rgba(59, 175, 218, 1)',
            borderWidth: 1,
            fill: false },

            {
                label: 'Bar',
                data: [
                    rsf(),
                    rsf(),
                    rsf(),
                    rsf(),
                    rsf(),
                    rsf()],

                backgroundColor: 'rgba(255, 206, 86, 0.2)',
                borderColor: 'rgba(255, 206, 86, 1)',
                borderWidth: 1,
                fill: false }] },


    options: {
        responsive: true,
        scales: {
            yAxes: [{
                ticks: {
                    beginAtZero: true } }] } } });






var ctx3 = document.getElementById("chart-3").getContext('2d');
var myChart3 = new Chart(ctx3, {
    type: 'pie',
    data: {
        labels: ["Red", "Blue", "Yellow", "Green", "Purple", "Orange"],
        datasets: [{
            label: 'Colors',
            data: [
                rsf(),
                rsf(),
                rsf(),
                rsf(),
                rsf(),
                rsf()],

            backgroundColor: [
                'rgba(255, 99, 132, 0.2)',
                'rgba(54, 162, 235, 0.2)',
                'rgba(255, 206, 86, 0.2)',
                'rgba(75, 192, 192, 0.2)',
                'rgba(153, 102, 255, 0.2)',
                'rgba(255, 159, 64, 0.2)'],

            borderColor: [
                'rgba(255,99,132,1)',
                'rgba(54, 162, 235, 1)',
                'rgba(255, 206, 86, 1)',
                'rgba(75, 192, 192, 1)',
                'rgba(153, 102, 255, 1)',
                'rgba(255, 159, 64, 1)'],

            borderWidth: 1 }] },


    options: {
        responsive: true,
        legend: {
            position: 'right' } } });




var ctx4 = document.getElementById("chart-4").getContext('2d');
var myChart4 = new Chart(ctx4, {
    type: 'doughnut',
    data: {
        labels: ["Red", "Blue", "Yellow", "Green", "Purple", "Orange"],
        datasets: [{
            label: 'Colors',
            data: [
                rsf(),
                rsf(),
                rsf(),
                rsf(),
                rsf(),
                rsf()],

            backgroundColor: [
                'rgba(255, 99, 132, 0.2)',
                'rgba(54, 162, 235, 0.2)',
                'rgba(255, 206, 86, 0.2)',
                'rgba(75, 192, 192, 0.2)',
                'rgba(153, 102, 255, 0.2)',
                'rgba(255, 159, 64, 0.2)'],

            borderColor: [
                'rgba(255,99,132,1)',
                'rgba(54, 162, 235, 1)',
                'rgba(255, 206, 86, 1)',
                'rgba(75, 192, 192, 1)',
                'rgba(153, 102, 255, 1)',
                'rgba(255, 159, 64, 1)'],

            borderWidth: 1 }] },


    options: {
        responsive: true,
        legend: {
            position: 'left' } } });




var ctx5 = document.getElementById("chart-5").getContext('2d');
var myChart5 = new Chart(ctx5, {
    type: 'bubble',
    data: {
        animation: {
            duration: 10000 },

        datasets: [{
            label: "Foo",
            backgroundColor: 'rgba(255, 99, 132, 0.2)',
            borderColor: 'rgba(255, 99, 132, 1)',
            borderWidth: 1,
            data: [{
                x: rsf(),
                y: rsf(),
                r: Math.abs(rsf()) / 5 },
                {
                    x: rsf(),
                    y: rsf(),
                    r: Math.abs(rsf()) / 5 },
                {
                    x: rsf(),
                    y: rsf(),
                    r: Math.abs(rsf()) / 5 },
                {
                    x: rsf(),
                    y: rsf(),
                    r: Math.abs(rsf()) / 5 },
                {
                    x: rsf(),
                    y: rsf(),
                    r: Math.abs(rsf()) / 5 },
                {
                    x: rsf(),
                    y: rsf(),
                    r: Math.abs(rsf()) / 5 },
                {
                    x: rsf(),
                    y: rsf(),
                    r: Math.abs(rsf()) / 5 }] },

            {
                label: "Bar",
                backgroundColor: 'rgba(255, 206, 86, 0.2)',
                borderColor: 'rgba(255, 206, 86, 1)',
                borderWidth: 1,
                data: [{
                    x: rsf(),
                    y: rsf(),
                    r: Math.abs(rsf()) / 5 },
                    {
                        x: rsf(),
                        y: rsf(),
                        r: Math.abs(rsf()) / 5 },
                    {
                        x: rsf(),
                        y: rsf(),
                        r: Math.abs(rsf()) / 5 },
                    {
                        x: rsf(),
                        y: rsf(),
                        r: Math.abs(rsf()) / 5 },
                    {
                        x: rsf(),
                        y: rsf(),
                        r: Math.abs(rsf()) / 5 },
                    {
                        x: rsf(),
                        y: rsf(),
                        r: Math.abs(rsf()) / 5 },
                    {
                        x: rsf(),
                        y: rsf(),
                        r: Math.abs(rsf()) / 5 }] },

            {
                label: "Baz",
                backgroundColor: 'rgba(54, 162, 235, 0.2)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 1,
                data: [{
                    x: rsf(),
                    y: rsf(),
                    r: Math.abs(rsf()) / 5 },
                    {
                        x: rsf(),
                        y: rsf(),
                        r: Math.abs(rsf()) / 5 },
                    {
                        x: rsf(),
                        y: rsf(),
                        r: Math.abs(rsf()) / 5 },
                    {
                        x: rsf(),
                        y: rsf(),
                        r: Math.abs(rsf()) / 5 },
                    {
                        x: rsf(),
                        y: rsf(),
                        r: Math.abs(rsf()) / 5 },
                    {
                        x: rsf(),
                        y: rsf(),
                        r: Math.abs(rsf()) / 5 },
                    {
                        x: rsf(),
                        y: rsf(),
                        r: Math.abs(rsf()) / 5 }] }] },



    options: {
        responsive: true,
        title: {
            display: true,
            text: 'Chart.js Bubble Chart' },

        tooltips: {
            mode: 'point' } } });