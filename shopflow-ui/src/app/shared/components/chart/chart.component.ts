import { Component, Input, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BaseChartDirective, NgChartsModule } from 'ng2-charts';
import { ChartConfiguration, ChartType } from 'chart.js';
import './chart-config';

@Component({
  selector: 'app-chart',
  standalone: true,
  imports: [CommonModule, NgChartsModule],
  template: `
    <div class="w-full chart-container">
      <div *ngIf="chartData && chartOptions" style="position: relative;">
        <div [style.height]="type === 'pie' || type === 'doughnut' ? '300px' : '400px'">
          <canvas 
            baseChart
            [data]="chartData"
            [options]="chartOptions"
            [type]="chartType"
          ></canvas>
        </div>
        
        <!-- Légende personnalisée pour pie/doughnut -->
        <div *ngIf="(type === 'pie' || type === 'doughnut') && pieLabels.length > 0" 
             class="mt-6 space-y-2">
          <div *ngFor="let item of pieLabels; let i = index" 
               class="flex items-center justify-between py-2 px-3 hover:bg-slate-50 rounded-lg transition-colors">
            <div class="flex items-center gap-3">
              <div class="w-3 h-3 rounded-full flex-shrink-0" 
                   [style.background-color]="item.color"></div>
              <span class="text-sm font-medium text-slate-700">{{ item.label }}</span>
            </div>
            <span class="text-sm font-bold text-slate-600">({{ item.value }})</span>
          </div>
        </div>
      </div>
      
      <div *ngIf="!data || data.length === 0" class="w-full h-64 flex flex-col items-center justify-center bg-slate-50/30 rounded-2xl border border-dashed border-slate-200">
        <p class="text-slate-400 font-bold text-sm">Aucune donnée disponible</p>
      </div>
    </div>
  `,
  styles: [`
    :host { 
      display: block; 
      width: 100%; 
    }
  `]
})
export class ChartComponent implements OnChanges {
  @Input() data: any[] = [];
  @Input() type: 'bar' | 'line' | 'pie' | 'doughnut' | 'radar' = 'bar';
  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;

  chartData: ChartConfiguration['data'] | null = null;
  chartOptions: ChartConfiguration['options'] | null = null;
  chartType: ChartType = 'bar';
  pieLabels: Array<{label: string, value: number, color: string}> = [];

  ngOnChanges(changes: SimpleChanges): void {
    if (this.data && this.data.length > 0) {
      this.updateChart();
    }
  }

  private updateChart(): void {
    const labels = this.data.map(item => item[0]);
    const values = this.data.map(item => item[1]);
    const maxValue = Math.max(...values);

    this.chartType = this.type as ChartType;

    if (this.type === 'bar') {
      // Utiliser un line chart au lieu de bar pour plus d'élégance
      this.chartType = 'line';
      
      this.chartData = {
        labels: labels,
        datasets: [{
          label: 'Revenus (DT)',
          data: values,
          borderColor: 'rgba(16, 185, 129, 1)',
          backgroundColor: 'rgba(16, 185, 129, 0.1)',
          borderWidth: 3,
          fill: true,
          tension: 0.4,
          pointBackgroundColor: 'rgba(16, 185, 129, 1)',
          pointBorderColor: '#ffffff',
          pointBorderWidth: 3,
          pointRadius: 6,
          pointHoverRadius: 8,
          pointHoverBackgroundColor: 'rgba(16, 185, 129, 1)',
          pointHoverBorderColor: '#ffffff',
          pointHoverBorderWidth: 3
        }]
      };

      this.chartOptions = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false
          },
          tooltip: {
            backgroundColor: 'rgba(15, 118, 110, 0.95)',
            padding: 16,
            titleFont: {
              size: 15,
              weight: 'bold'
            },
            bodyFont: {
              size: 14,
              weight: 'bold'
            },
            borderColor: 'rgba(16, 185, 129, 1)',
            borderWidth: 2,
            displayColors: false,
            callbacks: {
              label: (context: any) => {
                return `${context.parsed.y.toFixed(2)} DT`;
              }
            }
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            max: Math.ceil(maxValue * 1.2),
            ticks: {
              callback: (value: any) => `${value} DT`,
              font: {
                size: 13,
                weight: 'bold'
              },
              color: '#64748b',
              stepSize: Math.ceil(maxValue / 4)
            },
            grid: {
              color: 'rgba(226, 232, 240, 0.5)',
              lineWidth: 1
            },
            title: {
              display: true,
              text: 'Revenus (DT)',
              font: {
                size: 14,
                weight: 'bold'
              },
              color: '#475569',
              padding: { top: 10, bottom: 10 }
            }
          },
          x: {
            ticks: {
              font: {
                size: 13,
                weight: 'bold'
              },
              color: '#475569'
            },
            grid: {
              display: false
            },
            title: {
              display: true,
              text: 'Mois',
              font: {
                size: 14,
                weight: 'bold'
              },
              color: '#475569',
              padding: { top: 10, bottom: 0 }
            }
          }
        }
      };
    } else if (this.type === 'pie' || this.type === 'doughnut') {
      const colors = ['#6366f1', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899', '#06b6d4', '#84cc16'];
      
      // Créer les labels pour la légende personnalisée
      this.pieLabels = labels.map((label, i) => ({
        label: label,
        value: values[i],
        color: colors[i % colors.length]
      }));
      
      this.chartData = {
        labels: labels,
        datasets: [{
          data: values,
          backgroundColor: colors.slice(0, values.length),
          borderColor: '#ffffff',
          borderWidth: 2,
          hoverOffset: 10
        }]
      };

      this.chartOptions = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false // Désactiver la légende par défaut
          },
          tooltip: {
            backgroundColor: 'rgba(15, 118, 110, 0.95)',
            padding: 16,
            titleFont: {
              size: 15,
              weight: 'bold'
            },
            bodyFont: {
              size: 14,
              weight: 'bold'
            },
            borderColor: 'rgba(16, 185, 129, 1)',
            borderWidth: 2,
            callbacks: {
              label: (context: any) => {
                const label = context.label || '';
                const value = context.parsed;
                const total = context.dataset.data.reduce((a: number, b: number) => a + b, 0);
                const percentage = ((value / total) * 100).toFixed(1);
                return `${label}: ${value} produits (${percentage}%)`;
              }
            }
          }
        }
      };
    } else if (this.type === 'line') {
      this.chartData = {
        labels: labels,
        datasets: [{
          label: 'Revenus (DT)',
          data: values,
          borderColor: 'rgba(16, 185, 129, 1)',
          backgroundColor: 'rgba(16, 185, 129, 0.1)',
          borderWidth: 3,
          fill: true,
          tension: 0.4,
          pointBackgroundColor: 'rgba(16, 185, 129, 1)',
          pointBorderColor: '#ffffff',
          pointBorderWidth: 2,
          pointRadius: 5,
          pointHoverRadius: 7
        }]
      };

      this.chartOptions = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false
          },
          tooltip: {
            backgroundColor: 'rgba(15, 118, 110, 0.9)',
            padding: 12,
            titleFont: {
              size: 14,
              weight: 'bold'
            },
            bodyFont: {
              size: 13
            },
            callbacks: {
              label: (context: any) => {
                return `${context.parsed.y.toFixed(2)} DT`;
              }
            }
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            max: Math.ceil(maxValue * 1.2),
            ticks: {
              callback: (value: any) => `${value} DT`,
              font: {
                size: 12,
                weight: 'bold'
              },
              color: '#64748b'
            },
            grid: {
              color: 'rgba(226, 232, 240, 0.5)'
            }
          },
          x: {
            ticks: {
              font: {
                size: 12,
                weight: 'bold'
              },
              color: '#475569'
            },
            grid: {
              display: false
            }
          }
        }
      };
    } else if (this.type === 'radar') {
      this.chartData = {
        labels: labels,
        datasets: [{
          label: 'Revenus (DT)',
          data: values,
          borderColor: 'rgba(16, 185, 129, 1)',
          backgroundColor: 'rgba(16, 185, 129, 0.2)',
          borderWidth: 2,
          pointBackgroundColor: 'rgba(16, 185, 129, 1)',
          pointBorderColor: '#ffffff',
          pointBorderWidth: 2,
          pointRadius: 4,
          pointHoverRadius: 6
        }]
      };

      this.chartOptions = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false
          },
          tooltip: {
            backgroundColor: 'rgba(15, 118, 110, 0.9)',
            padding: 12,
            callbacks: {
              label: (context: any) => {
                return `${context.parsed.r.toFixed(2)} DT`;
              }
            }
          }
        },
        scales: {
          r: {
            beginAtZero: true,
            max: Math.ceil(maxValue * 1.2),
            ticks: {
              callback: (value: any) => `${value} DT`,
              font: {
                size: 11
              },
              color: '#64748b'
            },
            grid: {
              color: 'rgba(226, 232, 240, 0.5)'
            },
            pointLabels: {
              font: {
                size: 12,
                weight: 'bold'
              },
              color: '#475569'
            }
          }
        }
      };
    }

    // Force chart update
    if (this.chart) {
      this.chart.update();
    }
  }
}
