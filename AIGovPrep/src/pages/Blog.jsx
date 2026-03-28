import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Calendar, Clock, ChevronRight, BookOpen } from 'lucide-react';
// We simulate loading from json by importing it statically (Vite supports this automatically).
import blogPosts from '../data/blogPosts.json';

export default function Blog() {
  const [posts, setPosts] = useState([]);

  useEffect(() => {
    // In a real app we would fetch the JSON if it changes or from a CDN.
    // For local auto-generated Vite app, importing the JSON gives us the data.
    setPosts(blogPosts || []);
  }, []);

  return (
    <div className="page-wrapper animate-fadeIn">
      <div className="page-with-sidebar">
        <section className="page-header text-center" style={{ textAlign: 'center', marginBottom: '40px' }}>
          <div className="badge badge-primary animate-fadeInUp" style={{ marginBottom: '16px' }}>Sarkari AI Articles</div>
          <h1 className="animate-fadeInUp" style={{ animationDelay: '0.1s', marginBottom: '16px' }}>
            Latest <span className="text-gradient">Resources & Updates</span>
          </h1>
          <p className="animate-fadeInUp" style={{ animationDelay: '0.2s', maxWidth: '600px', margin: '0 auto' }}>
            Discover high-quality guides, exam strategies, and syllabus updates crafted precisely for aspirants.
          </p>
        </section>

        {posts.length === 0 ? (
          <div className="text-center" style={{ padding: '40px', background: 'var(--bg-tertiary)', borderRadius: '16px' }}>
            <BookOpen size={48} className="text-muted" style={{ margin: '0 auto 16px' }} />
            <p className="text-muted">No articles found yet. Run `npm run generate-blog` to auto-generate content.</p>
          </div>
        ) : (
          <div className="grid-3">
            {posts.map((post, idx) => (
              <Link to={`/blog/${post.slug}`} key={post.id} className="card animate-fadeInUp" style={{ animationDelay: `${0.3 + (idx * 0.1)}s`, textDecoration: 'none', display: 'flex', flexDirection: 'column' }}>
                <div style={{ display: 'flex', gap: '8px', marginBottom: '16px', flexWrap: 'wrap' }}>
                  {post.tags?.slice(0, 2).map(tag => (
                    <span key={tag} className="badge" style={{ background: 'var(--primary-bg)', color: 'var(--primary)', fontSize: '0.75rem', padding: '4px 8px' }}>
                      {tag}
                    </span>
                  ))}
                </div>
                <h3 style={{ marginBottom: '12px', fontSize: '1.25rem', lineHeight: 1.4, color: 'var(--text-primary)' }}>{post.title}</h3>
                <p style={{ color: 'var(--text-secondary)', marginBottom: '20px', flex: 1, fontSize: '0.95rem' }}>
                  {post.excerpt}
                </p>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', color: 'var(--text-muted)', fontSize: '0.85rem' }}>
                   <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                     <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                       <Calendar size={14} /> {new Date(post.date).toLocaleDateString()}
                     </span>
                     <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                       <Clock size={14} /> {post.readTime}
                     </span>
                   </div>
                   <ChevronRight size={16} className="text-primary" />
                </div>
              </Link>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
