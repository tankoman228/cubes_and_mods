import * as Toasts from "/public/js/toasts.js";

Vue.use(BootstrapVue);
Vue.use(Toasted);

console.log("stats.js");

new Vue({
  el: '#app',
  data: {
    hosts: [],
    servers: []
  },
  mounted() {
    this.fetchStats();
  },
  methods: {
    fetchStats() {
      axios
        .get('/api/stats/host')
        .then(resp => { this.hosts = resp.data; })
        .catch(err => {
          console.error(err);
          Toasts.showErrorToast('Ошибка загрузки хостов: ' + err.message);
        }).finally(() => {
            // отрисуем графики после любого из запросов
            this.$nextTick(() => this.drawHostsCharts());
        });

      axios
        .get('/api/stats/server')
        .then(resp => { 
          this.servers = resp.data; 
          
        })
        .catch(err => {
          console.error(err);
          Toasts.showErrorToast('Ошибка загрузки серверов: ' + err.message);
        })
        .finally(() => {
          // отрисуем графики после любого из запросов
          this.$nextTick(() => this.drawServersCharts());
        });
    },
    drawServersCharts() {
      
        this.servers.forEach(dto => {
            const id = dto.target.id;
            const name = dto.target.name || `Server #${id}`;
            const labels = dto.stats.map(e => new Date(e.timestamp));
        
            console.log(dto.stats[0]);
            console.log(dto.stats[0].memoryFree);

            const cpu = dto.stats.map(e => +e.cpuThreadsFree);
            const ram = dto.stats.map(e => +e.ramFree);
            const disk = dto.stats.map(e => +e.memoryFree);
        
            this.makeChart(`srv-${id}`, name, labels, [
            { label: 'ЦП (свободно потоков)', data: cpu, color: 'rgb(75, 192, 192)', axis: 'y'  },
            { label: 'ОЗУ (ГБ)', data: ram, color: 'rgb(255, 99, 132)', axis: 'y1'  },
            { label: 'ПЗУ (КБ)', data: disk, color: 'rgb(255, 205, 86)', axis: 'y2'  }
            ]);
        });
    },
    drawHostsCharts() {
        this.hosts.forEach(dto => {
            const id = dto.target.id;
            const name = dto.target.name || `Host #${id}`;
            const labels = dto.stats.map(e => new Date(e.timestamp));
        
            const runtime = dto.stats.map(e => +e.seconds_working);
            const limit_runtime = dto.stats.map(e => +dto.target.tariffHost.hours_work_max * 3600 );   

            const memory_used = dto.stats.map(e => +e.memory_used);
            const limit_mem = dto.stats.map(e => +dto.target.tariffHost.memory_limit);    
        
            this.makeChart(`host-${id}`, name, labels, [
            { label: 'Рантайм (сек)', data: runtime, color: 'rgb(0, 39, 211)', axis: 'y1',pointRadius: 1, pointHoverRadius: 6 },
            { label: 'Ограничение рантайма (сек)', data: limit_runtime, color: 'rgb(0, 125, 184)', axis: 'y1' },
            { label: 'Использовано ПЗУ (КБ)', data: memory_used, color: 'rgb(88, 0, 19)', axis: 'y2' },
            { label: 'Ограничение ПЗУ (КБ)', data: limit_mem, color: 'rgb(207, 0, 0)', axis: 'y2' },
            ]);
        });
    },

    makeChart(canvasId, title, labels, datasets) {
        const ctx = document.getElementById(canvasId).getContext('2d');
        new Chart(ctx, {
          type: 'line',
          data: {
            labels,
            datasets: datasets.map(d => ({
              label: d.label,
              data: d.data,
              borderColor: d.color,
              backgroundColor: d.color,
              fill: false,
              tension: 0.1,
              yAxisID: d.axis || 'y', // по умолчанию основная ось
              pointRadius: 0,
              pointHoverRadius: 8
            }))
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
              mode: 'nearest',
              axis: 'x',
              intersect: false
            },
            scales: {
              x: {
                type: 'time',
                min: labels[0], // первый timestamp
                max: labels[labels.length - 1], // последний timestamp
                time: {
                    tooltipFormat: 'dd.MM.yyyy HH:mm',
                    displayFormats: {
                    hour: 'dd.MM HH:mm',  // формат оси X
                    day: 'dd.MM',
                    }
                },
                ticks: {
                    maxRotation: 45,
                    autoSkip: true,
                    maxTicksLimit: 7
                }
              },
              y: {
                type: 'linear',
                position: 'left',
                title: { display: true, text: '' }
              },
              y1: {
                type: 'linear',
                position: 'right',
                grid: { drawOnChartArea: false },
                title: { display: true, text: '' }
              },
              y2: {
                type: 'linear',
                position: 'right',
                offset: true,
                grid: { drawOnChartArea: false },
                title: { display: true, text: '' }
              }
            },
            plugins: {
              title: {
                display: false,
                text: title,
                font: { size: 16 }
              },
              zoom: {
                pan: { enabled: true, mode: 'x' },
                zoom: { wheel: { enabled: true }, pinch: { enabled: true }, mode: 'x' }
              },
              tooltip: { enabled: true },
              legend: { display: true }
            }
          }
        });
      }
  }
});