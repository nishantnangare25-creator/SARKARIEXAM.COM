// Exam configuration data
export const EXAMS = [
  { id: 'upsc', name: 'UPSC Civil Services', icon: '🏛️' },
  { id: 'mpsc', name: 'MPSC', icon: '📋' },
  { id: 'ssc', name: 'SSC CGL/CHSL', icon: '📝' },
  { id: 'banking', name: 'Banking (IBPS/SBI)', icon: '🏦' },
  { id: 'railway', name: 'Railway (RRB)', icon: '🚂' },
  { id: 'nda', name: 'NDA', icon: '🎖️' },
  { id: 'state_psc', name: 'State PSC', icon: '🗳️' },
];

export const SUBJECTS = {
  upsc: ['History', 'Geography', 'Polity', 'Economy', 'Science', 'Environment', 'Current Affairs', 'Ethics', 'Essay'],
  mpsc: ['History', 'Geography', 'Polity', 'Economy', 'Science', 'Marathi Language', 'Current Affairs', 'Agriculture'],
  ssc: ['Quantitative Aptitude', 'English', 'General Intelligence', 'General Awareness'],
  banking: ['Quantitative Aptitude', 'Reasoning', 'English', 'General Awareness', 'Computer Knowledge'],
  railway: ['Mathematics', 'General Intelligence', 'General Science', 'General Awareness'],
  nda: ['Mathematics', 'English', 'General Knowledge', 'Physics', 'Chemistry', 'Geography', 'History'],
  state_psc: ['History', 'Geography', 'Polity', 'Economy', 'Science', 'Current Affairs', 'Regional Language'],
};

export const PREP_LEVELS = ['beginner', 'intermediate', 'advanced'];

export const FORUM_CATEGORIES = [
  { id: 'current-affairs', icon: '📰' },
  { id: 'exam-strategies', icon: '🎯' },
  { id: 'doubt-solving', icon: '❓' },
  { id: 'general', icon: '💬' },
];
