#!/bin/bash

echo "=== CiblOrgaSport Billetterie - Test Runner ==="

# Naviguer vers le répertoire du projet
cd "$(dirname "$0")/billetterie"

# Fonction pour afficher l'aide
show_help() {
    echo "Usage: ./run-tests.sh [OPTION]"
    echo "Options:"
    echo "  -a, --all          Lancer tous les tests"
    echo "  -s, --specific     Lancer une classe de test spécifique"
    echo "  -c, --clean        Nettoyer puis lancer les tests"
    echo "  -r, --report       Générer un rapport de test"
    echo "  -h, --help         Afficher cette aide"
}

# Traitement des arguments
case "${1:-all}" in
    -a|--all|all)
        echo "Lancement de tous les tests..."
        mvn test
        ;;
    -s|--specific)
        echo "Classe de test à lancer (ex: TicketServiceImplTest): "
        read -r test_class
        echo "Lancement de la classe $test_class..."
        mvn test -Dtest="$test_class"
        ;;
    -c|--clean)
        echo "Nettoyage puis lancement des tests..."
        mvn clean test
        ;;
    -r|--report)
        echo "Génération du rapport de test..."
        mvn test surefire-report:report
        echo "Rapport disponible dans: target/site/surefire-report.html"
        ;;
    -h|--help)
        show_help
        ;;
    *)
        echo "Option inconnue: $1"
        show_help
        exit 1
        ;;
esac
