exportToExcel(data: IMepList[]) {
  // Préparation des données à exporter
  const exportData = data.map(mep => ({
    nom: mep.nom,
    prenom: mep.prenom,
    matricule: mep.matricule,
    post: mep.post,
    email: mep.email,
    critereEligibilite: mep.critereEligibilite,
    status: mep.status,
    dateExecution: mep.dateExecution
      ? new Date(mep.dateExecution).toLocaleDateString()
      : '',
    filiale: mep.filiale?.nom || mep.filiale?.libelle || '',
    campaign: mep.campaign?.nom || mep.campaign?.libelle || ''
  }));

  const worksheet = XLSX.utils.json_to_sheet(exportData);

  const headerStyle = {
    font: { bold: true, sz: 14 }
  };

  // Appliquer le style à la première ligne
  const headerKeys = Object.keys(exportData[0]);
  headerKeys.forEach((_, index) => {
    const colLetter = String.fromCharCode(65 + index); // A, B, C, ...
    const cell = `${colLetter}1`;
    if (worksheet[cell]) {
      worksheet[cell].s = headerStyle;
    }
  });

  const generalStyle = {
    font: { sz: 16 }
  };

  Object.keys(worksheet).forEach(cell => {
    if (!cell.endsWith('1') && worksheet[cell] && worksheet[cell].v) {
      worksheet[cell].s = generalStyle;
    }
  });

  worksheet['!cols'] = headerKeys.map(() => ({ wch: 15 }));

  const workbook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workbook, worksheet, 'Feuille1');

  XLSX.writeFile(workbook, `Meps ${this.formatDate()}.xlsx`);
}Modifie la foonction exportToExcel pour que les champs pris en compte soit: nom, prenom, matricule, post, email, critereEligibilite, status, dateExecution, filiale, campaign

export interface IBaseInterface {
  id?: number;
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
  updatedBy?: string;
}


export interface IMepList extends IBaseInterface{

  nom: string;
  prenom: string;
  matricule: string;
  post: string;
  email: string;
  critereEligibilite: string; 
  status: 'PREVU' | 'EN_COURS' | 'REALISE' | 'VALIDE' | 'A_VALIDER';
  statusMepPerson: 'EncoreMEP' | 'NouveauMEP' | 'AncienMEP';
  dateExecution: Date;
  filiale: Filiale;  
  campaign: ICampaign; 
  
}


const { VERIFICATEUR_ABC, VALIDEUR_ABC } = PERMITTED_ROLES;
@Component({
  selector: 'Mystatus-mep-list',
  templateUrl: './mep-list.component.html',
  styleUrls: ['./mep-list.component.scss'],
})
export class MepListComponent implements OnInit {
  showResults = false;
  filterForm!: FormGroup;
  campaigns: ICampaign[] = [];
  mepList: IMepList[] = [];
  filiales: Filiale[] = [];
  filteredList: any[] = [];
  searchParams: IMepSearchParams = DEFAULT_SEARCH_PARAMS;
  startIndex!: number;
  endIndex!: number;
  totalElements!: number;
  currentPage!: number;
  pageSize!: number;
  totalPages!: number;
  last!: boolean;
  first!: boolean;
  searchObject: MepSearch = {
    nom: '',
    matricule: '',
    post: '',
    email: '',
    critereEligibilite: '',
    statusMepPerson: '',
  };
  successMessage = '';
  errorMessage = '';
  isVerificateurOrValideur: boolean = false;
  filiale: IRef | undefined = undefined;

  constructor(
    private fb: FormBuilder,
    private dialogService: NbDialogService,
    private mepService: MepService,
    private utilsService: UtilsService,
    private userService: UserService
  ) {
    this.filterForm = this.fb.group({
      entity: new FormControl<number | null>(null),
      campaignId: new FormControl<number | null>(null),
    });
  }

  ngOnInit(): void {
    this.userService.getAuthenticatedUser().subscribe((user: IUser) => {
      const userRoles = user.roles?.map(
        (role: IUserRoleRef) => role.code
      ) as string[];
      console.log('User roles:', userRoles);

      if (!user.active || !userRoles) {
        this.isVerificateurOrValideur = false;
      }
      if (hasAnyRole(userRoles, [VERIFICATEUR_ABC, VALIDEUR_ABC])) {
        this.filiale = user.filiale;
        this.isVerificateurOrValideur = true;
        this.searchParams.query = 'id>0;filiale.id==' + this.filiale?.id;
      }
      this.loadMEPs(this.searchParams);
      this.loadFiliales();
      this.loadCampaigns();
      this.showResults = true;
    });
  }

  loadMEPs(searchParams: any): void {
    this.mepService.getAllMEPs(searchParams).subscribe(
      (data: any) => {
        console.log('Données MEP récupérées :', JSON.stringify(data, null, 2));
        this.mepList = data.content;
        this.totalElements = data.totalElements;
        this.currentPage = data.pageable.pageNumber;
        this.pageSize = data.pageable.pageSize;
        this.totalPages = data.totalPages;
        this.last = data.last;
        this.first = data.first;
        this.calculateIndices();
        this.showResults = true;
      },
      (error: any) => {
        console.error('Erreur lors du chargement des MEPs :', error);
      }
    );
  }

  loadFiliales(): void {
    this.mepService.getAllFiliales().subscribe(
      (data: any[]) => {
        this.filiales = data;
      },
      (error: any) => {
        console.error('Erreur lors du chargement des filiales :', error);
      }
    );
  }

  applyFilters(): void {
    const filters = this.filterForm;
    if (this.filiale && this.isVerificateurOrValideur) {
      this.searchParams.query = 'id>0;filiale.id==' + this.filiale?.id;
    } else {
      this.searchParams.query = 'id>0';
    }

    if (filters.get('entity')?.value) {
      this.searchParams.query =
        this.searchParams.query +
        ';filiale.id==' +
        filters.get('entity')?.value +
        '';
    }
    if (filters.get('campaignId')?.value) {
      this.searchParams.query =
        this.searchParams.query +
        ';campaign.id==' +
        filters.get('campaignId')?.value +
        '';
    }
    this.loadMEPs(this.searchParams);
  }
  applySearch(): void {
    if (this.filiale && this.isVerificateurOrValideur) {
      this.searchParams.query = 'id>0;filiale.id==' + this.filiale?.id;
    } else {
      this.searchParams.query = 'id>0';
    }
    for (const key in this.searchObject) {
      if (this.searchObject.hasOwnProperty(key))
        if (this.searchObject.nom && this.searchObject.nom !== '') {
          this.searchParams.query +=
            ';(nom=like=%' + this.searchObject.nom + '%';
          this.searchParams.query +=
            ',Prenom=like=%' + this.searchObject.nom + '%)';
        }
      if (this.searchObject.matricule && this.searchObject.matricule !== '') {
        this.searchParams.query +=
          ';(matricule=like=%' + this.searchObject.matricule + '%)';
      }
      if (this.searchObject.post && this.searchObject.post !== '') {
        this.searchParams.query +=
          ';(post=like=%' + this.searchObject.post + '%)';
      }
      if (this.searchObject.email && this.searchObject.email !== '') {
        this.searchParams.query +=
          ';(email=like=%' + this.searchObject.email + '%)';
      }
      if (
        this.searchObject.critereEligibilite &&
        this.searchObject.critereEligibilite !== ''
      ) {
        this.searchParams.query +=
          ';(critereEligibilite=like=%' +
          this.searchObject.critereEligibilite +
          '%)';
      }
      if (this.searchObject.status && this.searchObject.status !== '') {
        this.searchParams.query +=
          ';(status=like=%' + this.searchObject.status + '%)';
      }
      if (
        this.searchObject.statusMepPerson &&
        this.searchObject.statusMepPerson !== ''
      ) {
        this.searchParams.query +=
          ';(statusMepPerson=like=%' + this.searchObject.statusMepPerson + '%)';
      }
    }
    this.loadMEPs(this.searchParams);
  }

  resetFilters(): void {
    this.filterForm.reset();
    this.filteredList = [];
    this.showResults = false;
  }

  onEditMep(mep: IMepList): void {
    console.log('MEP sélectionné :', mep); // Vérification

    if (!mep.id) {
      console.error("Erreur : L'ID du MEP est manquant !");
      return; // Empêche d'ouvrir le modal si l'ID est absent
    }

    const ref = this.dialogService.open(EditMepModalComponent, {
      context: { mep },
      closeOnBackdropClick: false,
    });

    ref.onClose.subscribe((updatedMep: IMepList) => {
      if (updatedMep) {
        console.log('MEP modifié :', updatedMep);
        this.utilsService.displaySucess(
          'Le MEP a été modifié avec succès !',
          'Succès'
        );

        this.loadMEPs(this.searchParams); //  Recharge la liste des MEPs après modification
      }
    });
  }

  onDeleteMep(mep: IMepList): void {
    console.log('MEP sélectionné pour suppression :', mep); //  Vérification

    if (!mep.id) {
      console.error("Erreur : L'ID du MEP est manquant !");
      return; //  Empêche la suppression si l'ID est absent
    }

    const confirmDelete = window.confirm(
      'Souhaitez-vous confirmer la suppression de la ligne MEP ?'
    );
    if (confirmDelete) {
      this.mepService.deleteMep(mep.id).subscribe(
        () => {
          console.log('MEP supprimé !');
          this.utilsService.displaySucess(
            'Le MEP a été supprimé avec succès !',
            'Succès'
          );
          this.loadMEPs(this.searchParams); //  Recharge la liste après suppression
        },
        (error) => {
          console.error('Erreur lors de la suppression du MEP :', error);
          this.utilsService.displayError(
            "Une erreur s'est produite lors de la suppression du MEP.",
            'Erreur'
          );
        }
      );
    }
  }

  loadCampaigns(): void {
    this.mepService.getAllCampaigns().subscribe(
      (data: any) => {
        console.log('Campagnes récupérées :', JSON.stringify(data, null, 2));
        this.campaigns = data.content;
      },
      (error: any) => {
        console.error('Erreur lors du chargement des campagnes :', error);
      }
    );
  }

  onAddMep(): void {
    const ref = this.dialogService.open(AddMepModalComponent, {
      closeOnBackdropClick: false,
    });

    ref.onClose.subscribe((newMep: IMepList) => {
      if (newMep) {
        console.log('Nouveau MEP ajouté :', newMep);

        this.utilsService.displaySucess(
          'Le MEP a été ajouté avec succès !',
          'Succès'
        );

        this.loadMEPs(this.searchParams);
      }
    });
  }

  onElementPerPageChange(event: string) {
    this.searchParams = { ...this.searchParams, page: 0, size: +event };
    this.loadMEPs(this.searchParams);
  }
  onPageNumberChange(event: number) {
    this.searchParams = { ...this.searchParams, page: +event };
    this.loadMEPs(this.searchParams);
  }

  calculateIndices() {
    this.endIndex = Math.min(
      (this.currentPage + 1) * this.pageSize,
      this.totalElements
    );
    this.startIndex = this.endIndex ? this.currentPage * this.pageSize + 1 : 0;
  }

  //EXPORTATION
  formatDate():string{
    const now = new Date();
    console.log(now);
    return `${now.getDate().toString().padStart(2, '0')}-${(now.getMonth() + 1).toString().padStart(2, '0')}-${now.getFullYear()} ${now.getHours().toString().padStart(2, '0')}-${now.getMinutes().toString().padStart(2, '0')}-${now.getSeconds().toString().padStart(2, '0')}`;
  }
  exportToExcel(data: any[]) {
    
    const worksheet = XLSX.utils.json_to_sheet(data);
    const headerStyle = {
      font: { bold: true, sz: 14 } 
    };

    const cellRefs = ['A1', 'B1', 'C1', 'D1', 'E1', 'F1', 'G1', 'H1', 'I1', 'J1', 'K1']; 
    cellRefs.forEach(cell => {
      if (worksheet[cell]) {
        worksheet[cell].s = headerStyle;
      }
    });
    
    const generalStyle = {
      font: { sz: 16 } 
    };

    Object.keys(worksheet).forEach(cell => {
      if (!cellRefs.includes(cell) && worksheet[cell] && worksheet[cell].v) {
        worksheet[cell].s = generalStyle;
      }
    });

    
    worksheet['!cols'] = [
      { wch: 10 },
      { wch: 10 }, 
      { wch: 10 },
      { wch: 10 },
      { wch: 10 },
      { wch: 10 },
      { wch: 10 },
      { wch: 10 },
      { wch: 10 },
      { wch: 10 },
      { wch: 10 },
      
    ];
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Feuille1');

    XLSX.writeFile(workbook, `Meps ${this.formatDate()}.xlsx`);
  }
}


