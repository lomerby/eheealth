# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

This is a Mobile Legends: Bang Bang (MLBB) Drafting Simulator built with Python. The application simulates the hero drafting phase of ranked matches, allowing players to practice strategic team composition and counter-picking.

## Development Commands

### Running the Application
```bash
# Start the application
python main.py

# Run with Python 3 explicitly
python3 main.py
```

### Testing
```bash
# Run tests with pytest (when implemented)
pytest

# Run tests with Python's unittest module
python -m unittest discover tests

# Run tests in verbose mode
pytest -v
```

### Package Management
```bash
# Install dependencies (currently none required)
pip install -r requirements.txt

# Install new packages
pip install <package-name>

# Create virtual environment (recommended)
python -m venv venv
# Windows: venv\Scripts\activate
# Linux/Mac: source venv/bin/activate
```

## Architecture Overview

### Core Components

- **`main.py`**: Application entry point that initializes the hero database and draft simulator
- **`src/core/draft_simulator.py`**: Main draft simulation engine handling ban/pick phases and game logic
- **`src/data/hero_database.py`**: Hero data management with roles, statistics, and metadata

### Data Flow

1. Application starts by loading hero data into memory via `HeroDatabase`
2. `DraftSimulator` uses the hero database to manage draft sessions
3. Draft state tracks bans, picks, current phase, and team turns
4. Future implementation will include AI suggestions and team composition analysis

### Key Design Patterns

- **Dependency Injection**: `DraftSimulator` receives `HeroDatabase` as a constructor parameter
- **State Management**: Draft sessions maintain immutable state objects
- **Modular Architecture**: Clear separation between data layer, business logic, and entry points

### MLBB-Specific Context

- **Hero Roles**: Tank, Fighter, Assassin, Mage, Marksman, Support
- **Draft Phases**: Alternating ban and pick phases between two teams
- **Strategic Elements**: Counter-picks, team synergies, role coverage, and meta considerations

## Development Notes

### Current Implementation Status
- Basic hero database with sample data (10 heroes)
- Draft state tracking structure in place
- CLI placeholder ready for interactive interface
- TODO markers indicate areas needing implementation

### Next Development Areas
- Complete draft phase progression logic
- Implement AI suggestion algorithms
- Add comprehensive hero data (100+ heroes)
- Build interactive CLI or web interface
- Add counter-pick and synergy analysis

### Testing Strategy
- Python's unittest module available for testing
- pytest can be added for more advanced testing features
- Test directories are set up but empty
- Focus testing on draft logic, hero queries, and state transitions

### Data Considerations
- Hero data currently hardcoded in `hero_database.py`
- Future implementations should load from external JSON files or API
- Hero metadata includes win rates, ban rates, counters, and synergies
- Python's async/await used for future API integration
