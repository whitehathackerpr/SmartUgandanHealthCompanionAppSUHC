from flask import Flask, request, jsonify
import os
from datetime import datetime

app = Flask(__name__)

# Simple rule-based symptom checker
# This is a basic implementation that can be expanded with ML models later
symptom_rules = {
    # Fever-related conditions
    "fever": {
        "malaria": ["headache", "chills", "sweating", "fatigue"],
        "flu": ["cough", "sore throat", "runny nose", "body aches"],
        "typhoid": ["abdominal pain", "constipation", "headache", "weakness"]
    },
    # Cough-related conditions
    "cough": {
        "common cold": ["runny nose", "sneezing", "sore throat"],
        "bronchitis": ["chest pain", "wheezing", "shortness of breath", "fatigue"],
        "pneumonia": ["fever", "chest pain", "shortness of breath", "fatigue"]
    },
    # Stomach-related conditions
    "abdominal pain": {
        "gastritis": ["nausea", "vomiting", "bloating", "indigestion"],
        "food poisoning": ["nausea", "vomiting", "diarrhea", "fever"],
        "appendicitis": ["right lower abdominal pain", "nausea", "fever", "loss of appetite"]
    },
    # Headache-related conditions
    "headache": {
        "migraine": ["nausea", "sensitivity to light", "sensitivity to sound", "visual disturbances"],
        "tension headache": ["neck pain", "stress", "anxiety", "fatigue"],
        "sinusitis": ["facial pain", "nasal congestion", "runny nose", "fever"]
    }
}

# Recommendations for conditions
recommendations = {
    "malaria": "Seek medical attention immediately. Take plenty of fluids and rest. Anti-malarial medication is required.",
    "flu": "Rest, drink plenty of fluids, and take over-the-counter fever reducers. Seek medical attention if symptoms worsen.",
    "typhoid": "Seek medical attention immediately. Antibiotics are required. Drink plenty of fluids and rest.",
    "common cold": "Rest, drink plenty of fluids, and take over-the-counter cold medications if needed.",
    "bronchitis": "Rest, drink plenty of fluids, and use a humidifier. Seek medical attention if symptoms persist.",
    "pneumonia": "Seek medical attention immediately. Antibiotics may be required. Rest and drink plenty of fluids.",
    "gastritis": "Avoid spicy and acidic foods. Take antacids and seek medical attention if symptoms persist.",
    "food poisoning": "Drink plenty of fluids to prevent dehydration. Seek medical attention if symptoms are severe.",
    "appendicitis": "Seek emergency medical attention immediately. Surgery may be required.",
    "migraine": "Rest in a dark, quiet room. Take over-the-counter pain relievers. Seek medical attention if migraines are frequent.",
    "tension headache": "Rest, manage stress, and take over-the-counter pain relievers if needed.",
    "sinusitis": "Use a humidifier, take over-the-counter decongestants, and seek medical attention if symptoms persist."
}

@app.route('/diagnose', methods=['POST'])
def diagnose():
    """
    Endpoint for diagnosing symptoms.
    
    Expected JSON payload:
    {
        "symptoms": ["fever", "cough", "fatigue"]
    }
    
    Returns:
    {
        "condition": "Malaria or Flu",
        "recommendation": "Drink fluids, rest, and visit clinic if symptoms persist",
        "confidence": 78
    }
    """
    try:
        data = request.get_json()
        
        if not data or 'symptoms' not in data:
            return jsonify({
                "error": "Invalid request. Please provide a list of symptoms."
            }), 400
        
        symptoms = data['symptoms']
        
        if not symptoms or not isinstance(symptoms, list):
            return jsonify({
                "error": "Invalid symptoms format. Please provide a non-empty list of symptoms."
            }), 400
        
        # Log the request for analysis
        log_request(symptoms)
        
        # Process the symptoms and get a diagnosis
        diagnosis = process_symptoms(symptoms)
        
        return jsonify(diagnosis), 200
    
    except Exception as e:
        return jsonify({
            "error": f"An error occurred: {str(e)}"
        }), 500

def process_symptoms(symptoms):
    """
    Process the symptoms and return a diagnosis.
    
    Args:
        symptoms (list): List of symptoms
    
    Returns:
        dict: Diagnosis result with condition, recommendation, and confidence
    """
    possible_conditions = {}
    
    # Check each symptom against our rules
    for symptom in symptoms:
        if symptom in symptom_rules:
            for condition, related_symptoms in symptom_rules[symptom].items():
                # Calculate how many of the related symptoms are present
                matching_symptoms = [s for s in symptoms if s in related_symptoms]
                
                # Calculate a simple confidence score
                # Base score for having the primary symptom
                base_score = 40
                
                # Additional score for each matching related symptom
                additional_score = 60 * (len(matching_symptoms) / len(related_symptoms))
                
                confidence = min(base_score + additional_score, 95)  # Cap at 95%
                
                # Add or update the condition in our possibilities
                if condition in possible_conditions:
                    # Take the higher confidence if we've seen this condition before
                    possible_conditions[condition] = max(
                        possible_conditions[condition], 
                        confidence
                    )
                else:
                    possible_conditions[condition] = confidence
    
    # If we have no matches, return a generic response
    if not possible_conditions:
        return {
            "condition": "Unknown",
            "recommendation": "Please consult a healthcare professional for proper diagnosis.",
            "confidence": 0
        }
    
    # Find the condition with the highest confidence
    best_condition = max(possible_conditions.items(), key=lambda x: x[1])
    condition_name = best_condition[0]
    confidence = int(best_condition[1])
    
    # Get the recommendation for this condition
    recommendation = recommendations.get(
        condition_name,
        "Please consult a healthcare professional for proper diagnosis."
    )
    
    # If confidence is low, indicate uncertainty
    if confidence < 50:
        condition_display = f"Possibly {condition_name}"
        recommendation = "Symptoms are not specific enough. " + recommendation
    else:
        condition_display = condition_name.title()
    
    return {
        "condition": condition_display,
        "recommendation": recommendation,
        "confidence": confidence
    }

def log_request(symptoms):
    """
    Log the request for analysis.
    
    Args:
        symptoms (list): List of symptoms
    """
    try:
        os.makedirs('logs', exist_ok=True)
        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        with open('logs/requests.log', 'a') as f:
            f.write(f"{timestamp} - Symptoms: {', '.join(symptoms)}\n")
    except Exception as e:
        print(f"Error logging request: {str(e)}")

if __name__ == '__main__':
    # Create logs directory if it doesn't exist
    os.makedirs('logs', exist_ok=True)
    
    # Run the Flask app
    app.run(host='0.0.0.0', port=5000, debug=True)