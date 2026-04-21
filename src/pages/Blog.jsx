import React, { useState, useEffect, useMemo } from 'react';
import { Link } from 'react-router-dom';
import { Calendar, Clock, ChevronRight, BookOpen, Search, Filter, ArrowLeft, ArrowRight, Sparkles } from 'lucide-react';
import blogPosts from '../data/blogPosts.json';

const POSTS_PER_PAGE = 9;

export default function Blog() {
  const [posts, setPosts] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [activeTag, setActiveTag] = useState('All');
  const [currentPage, setCurrentPage] = useState(1);

  useEffect(() => {
    const sortedPosts = [...(blogPosts || [])].sort((a, b) => 
      new Date(b.date).getTime() - new Date(a.date).getTime()
    );
    setPosts(sortedPosts);
  }, []);


  const allTags = useMemo(() => {
    const tags = new Set(['All']);
    posts.forEach(p => p.tags?.forEach(t => tags.add(t)));
    return Array.from(tags);
  }, [posts]);

  const filteredPosts = useMemo(() => {
    return posts.filter(post => {
      const matchesSearch = post.title.toLowerCase().includes(searchTerm.toLowerCase()) || 
                           post.excerpt.toLowerCase().includes(searchTerm.toLowerCase());
      const matchesTag = activeTag === 'All' || post.tags?.includes(activeTag);
      return matchesSearch && matchesTag;
    });
  }, [posts, searchTerm, activeTag]);

  // Reset to page 1 when filtering
  useEffect(() => {
    setCurrentPage(1);
  }, [searchTerm, activeTag]);

  const totalPages = Math.ceil(filteredPosts.length / POSTS_PER_PAGE);
  const currentPosts = filteredPosts.slice((currentPage - 1) * POSTS_PER_PAGE, currentPage * POSTS_PER_PAGE);
  
  const featuredPost = filteredPosts[0];
  const gridPosts = currentPosts.filter(p => p.id !== featuredPost?.id || currentPage !== 1);

  const isNew = (date) => {
    const diff = Date.now() - new Date(date).getTime();
    return diff < 86400000; // 24 hours
  };

  return (
    <div className="page-wrapper animate-fadeIn">
      <div className="page-with-sidebar">
        <section className="page-header text-center" style={{ textAlign: 'center', marginBottom: '40px' }}>
          <div className="badge badge-primary animate-fadeInUp" style={{ marginBottom: '16px' }}>Sarkari AI Hub</div>
          <h1 className="animate-fadeInUp" style={{ animationDelay: '0.1s', marginBottom: '16px' }}>
            Latest <span className="text-gradient">Resources & Updates</span>
          </h1>
          <p className="animate-fadeInUp" style={{ animationDelay: '0.2s', maxWidth: '600px', margin: '0 auto' }}>
            Expert guides and AI insights to supercharge your exam preparation.
          </p>
        </section>

        {/* Search and Filter Section */}
        <section className="animate-fadeInUp" style={{ animationDelay: '0.3s', marginBottom: '32px' }}>
          <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: '20px', padding: '24px' }}>
            <div style={{ position: 'relative', width: '100%' }}>
              <Search style={{ position: 'absolute', left: '16px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} size={20} />
              <input 
                type="text" 
                placeholder="Search articles by title or content..." 
                className="input"
                style={{ paddingLeft: '48px', width: '100%', fontSize: '1.05rem' }}
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
            <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap', alignItems: 'center' }}>
              <Filter size={18} className="text-primary" style={{ marginRight: '8px' }} />
              {allTags.slice(0, 10).map(tag => (
                <button 
                  key={tag} 
                  onClick={() => setActiveTag(tag)}
                  className={`btn btn-sm ${activeTag === tag ? 'btn-primary' : 'btn-outline'}`}
                  style={{ borderRadius: '20px', padding: '6px 16px' }}
                >
                  {tag}
                </button>
              ))}
            </div>
          </div>
        </section>

        {posts.length === 0 ? (
          <div className="text-center" style={{ padding: '60px', background: 'var(--bg-tertiary)', borderRadius: '16px' }}>
            <BookOpen size={64} className="text-muted" style={{ margin: '0 auto 24px' }} />
            <p className="text-muted" style={{ fontSize: '1.2rem' }}>No articles found yet. Please check back later.</p>
          </div>
        ) : (
          <>
            {/* Featured Post (Hero) */}
            {currentPage === 1 && featuredPost && !searchTerm && activeTag === 'All' && (
              <Link to={`/blog/${featuredPost.slug}`} className="card featured-card animate-fadeInUp" style={{ textDecoration: 'none', display: 'flex', gap: '32px', padding: '0', overflow: 'hidden', marginBottom: '40px', background: 'var(--bg-glass)', border: '1px solid var(--border-color)', animationDelay: '0.4s' }}>
                <div style={{ flex: '1.2', height: '400px', overflow: 'hidden' }}>
                  <img src={featuredPost.featuredImage} alt={featuredPost.title} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                </div>
                <div style={{ flex: '1', padding: '40px', display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '16px' }}>
                    <span className="badge badge-primary"><Sparkles size={14} style={{ marginRight: 6 }} /> FEATURED</span>
                    {isNew(featuredPost.date) && <span className="badge badge-green">NEW</span>}
                  </div>
                  <h2 style={{ fontSize: '2.2rem', color: 'var(--text-primary)', marginBottom: '16px', lineHeight: 1.2 }}>{featuredPost.title}</h2>
                  <p style={{ color: 'var(--text-secondary)', fontSize: '1.1rem', marginBottom: '24px' }}>{featuredPost.excerpt}</p>
                  <div style={{ display: 'flex', gap: '20px', color: 'var(--text-muted)', fontSize: '0.9rem' }}>
                    <span style={{ display: 'flex', alignItems: 'center', gap: '6px' }}><Calendar size={16} /> {new Date(featuredPost.date).toLocaleDateString()}</span>
                    <span style={{ display: 'flex', alignItems: 'center', gap: '6px' }}><Clock size={16} /> {featuredPost.readTime}</span>
                  </div>
                </div>
              </Link>
            )}

            {/* Grid Posts */}
            <div className="grid-3 animate-fadeInUp" style={{ animationDelay: '0.5s' }}>
              {gridPosts.map((post) => (
                <Link to={`/blog/${post.slug}`} key={post.id} className="card" style={{ textDecoration: 'none', display: 'flex', flexDirection: 'column', height: '100%', padding: '0', overflow: 'hidden' }}>
                  <div style={{ height: '200px', overflow: 'hidden', position: 'relative' }}>
                    <img src={post.featuredImage} alt={post.title} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                    {isNew(post.date) && <span className="badge badge-green" style={{ position: 'absolute', top: '12px', right: '12px' }}>NEW</span>}
                  </div>
                  <div style={{ padding: '24px', display: 'flex', flexDirection: 'column', flex: 1 }}>
                    <h3 style={{ marginBottom: '12px', fontSize: '1.25rem', lineHeight: 1.4, color: 'var(--text-primary)' }}>{post.title}</h3>
                    <p style={{ color: 'var(--text-secondary)', marginBottom: '20px', flex: 1, fontSize: '0.95rem' }}>{post.excerpt}</p>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', color: 'var(--text-muted)', fontSize: '0.85rem', paddingTop: '16px', borderTop: '1px solid var(--border-light)' }}>
                       <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}><Calendar size={14} /> {new Date(post.date).toLocaleDateString()}</span>
                       <ChevronRight size={16} className="text-primary" />
                    </div>
                  </div>
                </Link>
              ))}
            </div>

            {/* Pagination */}
            {totalPages > 1 && (
              <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '16px', marginTop: '48px' }}>
                <button 
                  className="btn btn-outline" 
                  disabled={currentPage === 1}
                  onClick={() => setCurrentPage(p => p - 1)}
                >
                  <ArrowLeft size={18} style={{ marginRight: '8px' }} /> Previous
                </button>
                <div style={{ color: 'var(--text-secondary)', fontWeight: 600 }}>
                  Page {currentPage} of {totalPages}
                </div>
                <button 
                  className="btn btn-outline" 
                  disabled={currentPage === totalPages}
                  onClick={() => setCurrentPage(p => p + 1)}
                >
                  Next <ArrowRight size={18} style={{ marginLeft: '8px' }} />
                </button>
              </div>
            )}
          </>
        )}
      </div>
      <style>{`
        .featured-card:hover img { transform: scale(1.05); }
        .featured-card img { transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1); }
        @media (max-width: 992px) {
          .featured-card { flex-direction: column; }
          .featured-card div { flex: 1 !important; height: auto !important; }
        }
      `}</style>
    </div>
  );
}
