import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { DashboardService } from '../../../core/services/dashboard.service';
import { MatIconModule } from '@angular/material/icon';
import { ChartComponent } from '../../../shared/components/chart/chart.component';

@Component({
  selector: 'app-seller-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, MatIconModule, ChartComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  stats: any = {
    myProductsCount: 0,
    totalStock: 0,
    totalRevenue: 0,
    recentOrders: [],
    lowStock: [],
    chartData: [],
    categoryData: [],
    statusData: []
  }; // Stocke les statistiques (nb produits, stock, etc.)
  loading = true; // État de chargement

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.loadStats(); // Charge les données au démarrage
  }

  /**
   * Appelle le service pour récupérer les statistiques du vendeur connecté.
   */
  private loadStats(): void {
    this.dashboardService.getSellerStats().subscribe({
      next: (data) => {
        this.stats = data; // Met à jour les stats
        this.loading = false; // Fin chargement
      },
      error: (err) => {
        console.error('Erreur lors du chargement des stats vendeur', err);
        this.loading = false;
      }
    });
  }
}
